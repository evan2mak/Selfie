package evtomak.iu.edu.selfie

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// CameraFragment: Manages the camera operations and interfaces with Firebase for image storage.
class CameraFragment : Fragment() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture

    // requestPermissionLauncher: Handles the result of the camera permission request.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                view?.post { startCamera() }
            }
            else {
                Log.e(TAG, "Camera permission denied")
            }
        }

    // onCreateView: Inflates the layout for this fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    // onViewCreated: Sets up the camera and capture button post view creation.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        view.findViewById<Button>(R.id.captureButton).setOnClickListener {
            captureImage()
        }

        if (allPermissionsGranted()) {
            view.post { startCamera() }
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // onDestroyView: Shuts down the camera executor on view destruction.
    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    // startCamera: Initializes the camera and binds it to the lifecycle.
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                Log.d(TAG, "Camera bound to lifecycle.")
            }
            catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // allPermissionsGranted: Checks if camera permission is granted.
    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    // getOutputDirectory: Determines the output directory for storing photos.
    private fun getOutputDirectory(): File {
        val mediaDir = File(requireContext().filesDir, resources.getString(R.string.app_name)).apply { mkdirs() }
        return if (mediaDir.exists()) mediaDir else requireContext().filesDir
    }

    // captureImage: Captures an image and saves it to a file.
    private fun captureImage() {
        if (!this::imageCapture.isInitialized) {
            Log.e(TAG, "ImageCapture not initialized or not bound to a camera.")
            return
        }

        val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")
                    uploadImageToFirebaseStorage(savedUri)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    // uploadImageToFirebaseStorage: Uploads the captured image to Firebase Storage.
    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("selfies/$userId/${imageUri.lastPathSegment}")


        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Image uploaded to Firebase Storage")
                activity?.runOnUiThread {
                    findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
                }
            }
            else {
                Log.e(TAG, "Image upload failed: ${task.exception?.message}", task.exception)
            }
        }
    }
}
