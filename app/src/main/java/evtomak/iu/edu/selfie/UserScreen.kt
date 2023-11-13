package evtomak.iu.edu.selfie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

// UserScreen: Handles user authentication operations like sign in, sign up, and sign out.
class UserScreen : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var auth: FirebaseAuth

    // onCreateView: Inflates the layout for this fragment and initializes Firebase Auth.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_screen, container, false)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        auth = FirebaseAuth.getInstance()

        val factory = UserViewModelFactory(UserRepositorySingleton.getInstance())
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        return view
    }

    // onViewCreated: Sets up UI components and observes ViewModel for navigation and errors.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        view.findViewById<Button>(R.id.signInButton).setOnClickListener { onSignInClicked() }
        view.findViewById<Button>(R.id.signUpButton).setOnClickListener { onSignUpClicked() }
        view.findViewById<Button>(R.id.signOutButton).setOnClickListener { onSignOutClicked() }

        userViewModel.navigateToHomeScreen.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(R.id.action_userScreenFragment_to_homeFragment)
            }
        }

        userViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // onSignInClicked: Handles the sign-in process.
    private fun onSignInClicked() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and password required.", Toast.LENGTH_SHORT).show()
            return
        }

        userViewModel.login(email, password)
    }

    // onSignUpClicked: Handles the sign-up process.
    private fun onSignUpClicked() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and password required.", Toast.LENGTH_SHORT).show()
            return
        }

        userViewModel.register(email, password)
    }

    // onSignOutClicked: Handles the sign-out process.
    private fun onSignOutClicked() {
        userViewModel.logout()
        Toast.makeText(context, "Signed out successfully.", Toast.LENGTH_SHORT).show()
    }
}
