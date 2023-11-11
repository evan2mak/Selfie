package evtomak.iu.edu.selfie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth // FirebaseAuth instance for managing user authentication.
    private lateinit var navController: NavController // NavController for managing app navigation.
    private lateinit var userRepository: UserRepository // UserRepository for managing user data.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase and enable offline data persistence.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        auth = FirebaseAuth.getInstance()
        navController = findNavController(R.id.nav_host_fragment)
        userRepository = UserRepositorySingleton.getInstance()

        // Set up the FirebaseAuth.AuthStateListener to respond to changes in the user's sign-in state
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // User is not authenticated, navigate to UserScreen
                navController.navigate(R.id.userScreenFragment)
            }
            else {
                // User is authenticated, navigate to HomeFragment
                navController.navigate(R.id.homeFragment)
            }
        }
    }
}



