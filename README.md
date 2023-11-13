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




