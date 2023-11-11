package evtomak.iu.edu.selfie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is authenticated
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // User is not authenticated, navigate to UserScreen
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.userScreenFragment)
        } else {
            // User is authenticated, navigate to HomeFragment
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.homeFragment)
        }
    }
}


