import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquest.R

class PhotosAdapter(private val photoList: List<String>, private val onMenuClick: (Int) -> Unit) : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
        val overflowMenu: ImageButton = itemView.findViewById(R.id.overflowMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photoList[position]
        Glide.with(holder.photoImageView.context)
            .load(photoUrl)
            .into(holder.photoImageView)

        // Set a click listener for the overflow menu (three dots)
        holder.overflowMenu.setOnClickListener {
            onMenuClick(position)  // Handle menu click by passing the position
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}
