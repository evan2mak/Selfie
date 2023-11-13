package evtomak.iu.edu.selfie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// ImageAdapter: Manages the display of images in a RecyclerView.
class ImageAdapter(var images: MutableList<String>, private val listener: OnImageClickListener) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    // OnImageClickListener: Interface to handle image click events.
    interface OnImageClickListener {
        fun onImageClick(imageUri: String)
    }

    // ViewHolder: Holds the view for each image in the RecyclerView.
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view)
    }

    // onCreateViewHolder: Inflates the view for each image item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: Binds an image to the ViewHolder and sets up click listener.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images[position])
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            listener.onImageClick(images[position])
        }
    }

    // getItemCount: Returns the total number of images in the adapter.
    override fun getItemCount(): Int = images.size

    // updateImages: Updates the list of images and notifies the adapter of the change.
    fun updateImages(newImages: List<String>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }
}
