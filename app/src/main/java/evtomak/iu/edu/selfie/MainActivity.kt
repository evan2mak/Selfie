package evtomak.iu.edu.selfie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// MainActivity: The primary activity that manages navigation and Firebase authentication.
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var userRepository: UserRepository

    // onCreate: Initializes components and sets up navigation based on authentication status.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        auth = FirebaseAuth.getInstance()
        navController = findNavController(R.id.nav_host_fragment)
        userRepository = UserRepositorySingleton.getInstance()

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                navController.navigate(R.id.userScreenFragment)
            }
            else {
                navController.navigate(R.id.homeFragment)
            }
        }
    }
}



