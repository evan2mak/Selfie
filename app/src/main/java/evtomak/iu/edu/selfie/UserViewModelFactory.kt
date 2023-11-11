package evtomak.iu.edu.selfie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// UserViewModelFactory: Factory class for creating instances of UserViewModel.
class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    // create: Creates and returns an instance of UserViewModel or throws an exception if the ViewModel class is unknown.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
