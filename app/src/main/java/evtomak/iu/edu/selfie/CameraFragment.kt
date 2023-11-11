package evtomak.iu.edu.selfie

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.concurrent.ExecutorService

class CameraFragment : Fragment() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        previewView = view.findViewById(R.id.previewView)
        outputDirectory = getOutputDirectory()

        val captureButton = view.findViewById<Button>(R.id.captureButton)
        captureButton.setOnClickListener {
            captureImage()
        }

        startCamera()
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Set up the ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    private fun captureImage() {
        val imageCaptureOutputOptions = ImageCapture.OutputFileOptions.Builder(outputDirectory)
            .build()

        imageCapture.takePicture(
            imageCaptureOutputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    // Upload the captured image to Firebase Storage
                    if (savedUri != null) {
                        uploadImageToFirebaseStorage(savedUri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error capturing image: ${exception.message}", exception)
                }
            })
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("selfies/${System.currentTimeMillis()}.jpg")

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Image uploaded to Firebase Storage")
                findNavController().popBackStack()
            } else {
                Log.e(TAG, "Image upload failed: ${task.exception?.message}", task.exception)
            }
        }
    }
}
