package evtomak.iu.edu.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

// FullscreenImageFragment: Displays a full-screen image using Glide.
class FullscreenImageFragment : Fragment() {
    private var imageUri: String? = null

    // onCreate: Retrieves the image URI passed in the fragment's arguments.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getString("imageUri")
        }
    }

    // onCreateView: Inflates the layout and sets up the ImageView with the image URI.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.fullscreenImageView)

        Glide.with(this)
            .load(imageUri)
            .into(imageView)

        return view
    }
}

