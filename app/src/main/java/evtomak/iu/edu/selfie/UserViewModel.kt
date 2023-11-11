package evtomak.iu.edu.selfie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// UserViewModel: ViewModel for managing user authentication and registration.
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val user = userRepository.user
    val navigateToHomeScreen = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    // login: Attempts to log in the user with the provided email and password.
    fun login(email: String, password: String) {
        userRepository.login(email, password) { success, message ->
            if (success) {
                navigateToHomeScreen.postValue(true)
            }
            else {
                errorMessage.postValue(message.toString()) // Post error message
            }
        }
    }

    // register: Attempts to register a new user with the provided email and password.
    fun register(email: String, password: String) {
        userRepository.register(email, password) { success, message ->
            if (success) {
                navigateToHomeScreen.postValue(true)
            }
            else {
                errorMessage.postValue(message.toString()) // Post error message
            }
        }
    }

    // logout: Logs out the current authenticated user.
    fun logout() {
        userRepository.logout()
    }
}
