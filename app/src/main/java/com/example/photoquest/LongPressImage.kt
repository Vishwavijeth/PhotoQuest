package com.example.photoquest

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LongPressImage: DialogFragment() {

    private lateinit var imageUrl: String
    private lateinit var imageUser: String
    private lateinit var imageDescription: String
    private lateinit var photosList: MutableList<Photo>
    private lateinit var imageId: String

    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())

        imageUrl = arguments?.getString("image_url") ?: ""
        imageUser = arguments?.getString("image_user") ?: ""
        imageDescription = arguments?.getString("image_description") ?: ""

        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.layout_bottom_screen, null)


        val closeButton: ImageButton = view.findViewById(R.id.clearSymbol)
        val download: TextView = view.findViewById(R.id.downloadImage)
        val fav: TextView = view.findViewById(R.id.addToFav)

        download.setOnClickListener {
            DownloadImage(imageUrl, imageUser)
        }

        fav.setOnClickListener {
            AddToFavs(imageUrl = imageUrl, imageUser = imageUser, imageDesc = imageDescription)
        }

        closeButton.setOnClickListener {
            dismiss()
        }
        builder.setView(view)
        isCancelable = true

        return builder.create()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.layout_bottom_screen, container, false)

        val downloadBtn = root.findViewById<TextView>(R.id.downloadImage)
        val favoriteBtn = root.findViewById<TextView>(R.id.addToFav)


        return view
    }

    private fun DownloadImage(imageUrl: String, imageUser: String) {
        try {
            Log.d(TAG, "URL: $imageUrl")

            // Validate if the URL starts with http/https
            if (!imageUrl.startsWith("http")) {
                Toast.makeText(requireContext(), "Invalid image URL", Toast.LENGTH_SHORT).show()
                return
            }

            val request = DownloadManager.Request(Uri.parse(imageUrl))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Downloading $imageUser's image")
            request.setDescription("Downloading image")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "$imageUser.jpg")
            } else {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "$imageUser.jpg")
            }

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(requireContext(), "Download started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun AddToFavs(imageUrl: String, imageUser: String, imageDesc: String)
    {
        val userid = FirebaseAuth.getInstance().currentUser?.uid
        val photoid = arguments?.getString("image_userid") ?: ""

        if (userid != null)
        {
            val db = FirebaseFirestore.getInstance()
            val favorite = Favorite(userId = userid, userName = imageUser, description = imageDesc, imageUrl = imageUrl)

            db.collection("Favorite")
                .add(favorite)
                .addOnSuccessListener {
                    Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add favorite: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1001
        const val TAG = "LongPressImage"
    }
}