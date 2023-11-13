package evtomak.iu.edu.selfie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// UserRepository: Manages user authentication and registration using Firebase.

class UserRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    // Initializes the user LiveData based on the current Firebase authenticated user.
    init {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _user.value = User(currentUser.uid, currentUser.email!!)
        }
        else {
            _user.value = null
        }
    }

    // login: Authenticates a user using email and password.
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        _user.value = User(currentUser.uid, currentUser.email!!)
                        Log.d("FirebaseAuth", "Login successful")
                        callback(true, null)
                    }
                }
                else {
                    Log.e("FirebaseAuth", "Login failed", task.exception)
                    _user.value = null
                    callback(false, "Login failed. Please check your credentials.")
                }
            }
    }

    // register: Registers a new user using email and password.
    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        val user = User(currentUser.uid, currentUser.email!!)
                        _user.value = user
                        saveUserToDatabase(user)
                        Log.d("FirebaseAuth", "Registration successful")
                        callback(true, null)
                    }
                }
                else {
                    Log.e("FirebaseAuth", "Registration failed", task.exception)
                    _user.value = null
                    callback(false, "Registration failed. Account exists already or invalid credentials.")
                }
            }
    }

    // saveUserToDatabase: Saves the authenticated user's details to Firebase Database.
    private fun saveUserToDatabase(user: User) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(user.id).setValue(user)
    }

    // logout: Logs out the authenticated user.
    fun logout() {
        firebaseAuth.signOut()
        _user.value = null
    }
}
