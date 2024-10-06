package com.example.photoquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ImageFullScreen : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_image_full_screen, container, false)

        val imageView: ImageView = root.findViewById(R.id.fullscreenImageView)
        val descriptionTextView: TextView = root.findViewById(R.id.descriptionTextView)
        val userTextView: TextView = root.findViewById(R.id.userTextView)


        val imageUrl = arguments?.getString("image_url")
        val user = arguments?.getString("image_user")
        val description = arguments?.getString("image_desc")

        Log.d(ImageFullScreen.TAG, "url: ${imageUrl}, user: ${user}, desc: $description")

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }

        userTextView.text = user ?: "unknown"
        descriptionTextView.text = description ?: "no desc"


        return root
    }

    companion object {
        const val TAG = "ImageFullScreen"
    }
}