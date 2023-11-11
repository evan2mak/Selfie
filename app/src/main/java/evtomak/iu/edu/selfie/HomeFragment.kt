package evtomak.iu.edu.selfie

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment(), SensorEventListener {
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private val SHAKE_THRESHOLD = 1500 // Adjust this value based on your shake sensitivity preference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)

        // Initialize sensor
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Set up the RecyclerView with the adapter
        val images = mutableListOf<String>()
        val imageAdapter = ImageAdapter(images)
        imageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imageRecyclerView.adapter = imageAdapter

        // Fetch images from Firebase Storage
        fetchImagesFromFirebaseStorage(imageAdapter)

        return view
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            // Check for a shake
            if ((curTime - lastUpdate) > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = Math.sqrt((x * x + y * y + z * z).toDouble()) / diffTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    // Shake detected, navigate to the CameraFragment
                    findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun fetchImagesFromFirebaseStorage(imageAdapter: ImageAdapter) {
        val storageReference = FirebaseStorage.getInstance().reference.child("selfies")

        storageReference.listAll().addOnSuccessListener { result ->
            val imageUrls = mutableListOf<String>()
            for (item in result.items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    imageUrls.add(uri.toString())
                    imageAdapter.notifyDataSetChanged() // Update the adapter
                }
            }
        }.addOnFailureListener { exception ->
            // Handle failure
        }
    }
}
