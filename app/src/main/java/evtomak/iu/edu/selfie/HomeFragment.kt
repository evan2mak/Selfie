package evtomak.iu.edu.selfie

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

// HomeFragment: Displays user's images and handles shake gestures for navigation.
class HomeFragment : Fragment(), SensorEventListener, ImageAdapter.OnImageClickListener {
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private val SHAKE_THRESHOLD = 1000

    // onCreateView: Sets up the UI elements and fetches images from Firebase Storage.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val images = mutableListOf<String>()
        val imageAdapter = ImageAdapter(images, this)
        imageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imageRecyclerView.adapter = imageAdapter

        fetchImagesFromFirebaseStorage(imageAdapter)

        logoutButton.setOnClickListener {
            UserRepositorySingleton.getInstance().logout()
            findNavController().navigate(R.id.action_homeFragment_to_userScreenFragment)
        }

        return view
    }

    // onResume: Registers the sensor event listener when the fragment resumes.
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // onPause: Unregisters the sensor event listener when the fragment pauses.
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // onSensorChanged: Detects shake gesture and navigates to the camera fragment.
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = Math.sqrt((x * x + y * y + z * z).toDouble()) / diffTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
                }
            }
        }
    }

    // onAccuracyChanged: Required override for SensorEventListener, not used.
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    // fetchImagesFromFirebaseStorage: Fetches images from Firebase and updates the RecyclerView.
    private fun fetchImagesFromFirebaseStorage(imageAdapter: ImageAdapter) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageReference = FirebaseStorage.getInstance().reference.child("selfies/$userId")

        storageReference.listAll().addOnSuccessListener { result ->
            val imageUrls = mutableListOf<String>()
            for (item in result.items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    Log.d(TAG, "Fetched image URL: $uri")
                    imageUrls.add(uri.toString())
                    activity?.runOnUiThread {
                        imageAdapter.updateImages(imageUrls)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error fetching images", exception)
        }
    }

    // onImageClick: Navigates to the FullscreenImageFragment with the clicked image URI.
    override fun onImageClick(imageUri: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToFullscreenImageFragment(imageUri)
        findNavController().navigate(action)
    }
}
