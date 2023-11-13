# C323 Project 9 - Selfie App: Evan Tomak

This project is an app that allows users to take and store selfies. This project also features user sign in, sign up, and sign out capabilities through firebase authentication as well as a realtime database.

The functionality is described in more detail below:

## User Screen

[X] The user screen is where the user will have the ability to sign in, sign up, or sign out of the notes app.

[X] If the user does not have an account, they must sign up with their email and password.

[X] If the user does not have an account and they try to sign in or sign out, an error message will be thrown.

[X] The user also must input both the email and password. If either is empty, it will throw an error message when trying to sign up or sign in.

[X] If the user has an account, they must click sign in. If they click sign up, an error message will be thrown.

[X] Upon successful sign in or sign up, the user will be navigated to the notes list fragment.

## Home Fragment

[X] The home fragment displays the all of the selfies that the specified user has taken in a list.

[X] The user can scroll to see all of their selfies

[X] If the user would like to take a new selfie, they must shake the device (simulated in the gif below) to be transported to the camera fragment.

[X] If the user would like to logout/sign out, they can click the logout button to get back to the user screen.

## Camera Fragment

[X] This is where the user can take a selfie.

[X] When the user clicks the capture button, a selfie will be taken and stored in the home fragment. The user will automatically be taken back to the home fragment to see their selfie(s). 

## 

The following functions/extensions are implemented:

## CameraFragment

Manages the camera operations and interfaces with Firebase for image storage.

onCreateView: 

Inflates the layout for this fragment.

onViewCreated: 

Sets up the camera and capture button post view creation.

onDestroyView: 

Shuts down the camera executor on view destruction.

startCamera: 

Initializes the camera and binds it to the lifecycle.

allPermissionsGranted: 

Checks if camera permission is granted.

getOutputDirectory: 

Determines the output directory for storing photos.

captureImage: 

Captures an image and saves it to a file.

uploadImageToFirebaseStorage: 

Uploads the captured image to Firebase Storage.

## FullscreenImageFragment

Displays a full-screen image using Glide.

onCreate: 

Retrieves the image URI passed in the fragment's arguments.

onCreateView: 

Inflates the layout and sets up the ImageView with the image URI.

## HomeFragment

Displays user's images and handles shake gestures for navigation.

onCreateView: 

Sets up the UI elements and fetches images from Firebase Storage.

onResume: 

Registers the sensor event listener when the fragment resumes.

onPause: 

Unregisters the sensor event listener when the fragment pauses.

onSensorChanged: 

Detects shake gesture and navigates to the camera fragment.

onAccuracyChanged: 

Required override for SensorEventListener, not used.

fetchImagesFromFirebaseStorage: 

Fetches images from Firebase and updates the RecyclerView.

onImageClick: 

Navigates to the FullscreenImageFragment with the clicked image URI.

## ImageAdapter

Manages the display of images in a RecyclerView.

OnImageClickListener: 

Interface to handle image click events.

ViewHolder: 

Holds the view for each image in the RecyclerView.

onCreateViewHolder: 

Inflates the view for each image item.

onBindViewHolder: 

Binds an image to the ViewHolder and sets up click listener.

getItemCount: 

Returns the total number of images in the adapter.

updateImages: 

Updates the list of images and notifies the adapter of the change.

## MainActivity

The primary activity that manages navigation and Firebase authentication.

onCreate: 

Initializes components and sets up navigation based on authentication status.

## User

Data class for users.

## UserRepository

Manages user authentication and registration using Firebase.

init:

Initialize the user LiveData based on the current Firebase authenticated user.

login: 

Authenticates a user using email and password.

register: 

Registers a new user using email and password.

saveUserToDatabase: 

Saves the authenticated user's details to Firebase Database.

logout: 

Logs out the authenticated user.

## UserRepositorySingleton

Provides a singleton instance of UserRepository.

## UserScreen

Fragment for user authentication including sign in, sign up, and sign out.

onCreateView: 

Inflates the layout and initializes UI components.

onViewCreated: 

Sets up UI behavior and event listeners.

onDestroyView: 

Removes observers when the view is destroyed.

onSignInClicked: 

Handles sign in button click.

onSignUpClicked: 

Handles sign up button click.

onSignOutClicked: 

Handles sign out button click.

## UserViewModel

ViewModel for managing user authentication and registration.

login: 

Attempts to log in the user with the provided email and password.

register: 

Attempts to register a new user with the provided email and password.

logout: 

Logs out the current authenticated user.

## UserViewModelFactory

Factory class for creating instances of UserViewModel.

