package evtomak.iu.edu.selfie

// UserRepositorySingleton: Provides a singleton instance of UserRepository.
object UserRepositorySingleton {
    private var instance: UserRepository? = null

    // getInstance: Returns the singleton instance of UserRepository, creating it if necessary.
    fun getInstance(): UserRepository {
        if (instance == null) {
            instance = UserRepository()
        }
        return instance!!
    }
}
