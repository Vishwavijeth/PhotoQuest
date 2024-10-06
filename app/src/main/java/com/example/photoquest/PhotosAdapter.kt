import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquest.Photo
import com.example.photoquest.R

class PhotosAdapter(private val photoList: List<Photo>,
                    private val clickListener: (Photo) -> Unit,
                    private val longClickListener: (Photo) -> Unit)
    : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
        val photoImageUser: TextView = itemView.findViewById(R.id.photoImageUsername)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photoList[position]

        Glide.with(holder.photoImageView.context)
            .load(photoUrl.urls.regular)
            .into(holder.photoImageView)

        holder.photoImageUser.text = photoUrl.user.name

        holder.itemView.setOnClickListener {
            clickListener(photoUrl)
        }

        holder.itemView.setOnLongClickListener {
            longClickListener(photoUrl)
            true
        }

    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}
