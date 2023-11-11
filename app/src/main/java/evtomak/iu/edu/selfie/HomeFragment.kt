package evtomak.iu.edu.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {
    private lateinit var imageRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)

        // Set up the RecyclerView with the adapter
        val images = mutableListOf<String>()
        val imageAdapter = ImageAdapter(images)
        imageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imageRecyclerView.adapter = imageAdapter

        // Fetch images from Firebase Storage
        fetchImagesFromFirebaseStorage(imageAdapter)

        return view
    }

    private fun fetchImagesFromFirebaseStorage(imageAdapter: ImageAdapter) {
        val storageReference = FirebaseStorage.getInstance().reference.child("selfies")

        storageReference.listAll().addOnSuccessListener { result ->
            val imageUrls = mutableListOf<String>()
            for (item in result.items) {
                // Add each image URL to the list
                item.downloadUrl.addOnSuccessListener { uri ->
                    imageUrls.add(uri.toString())
                    imageAdapter.notifyDataSetChanged() // Update the adapter
                }
            }
        }.addOnFailureListener { exception ->
            // Handle the failure to fetch images
            // You can display an error message to the user if needed
        }
    }
}
