package com.example.photoquest

import PhotosAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private val accessKey = "ACCESS_KEY"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotosAdapter
    private var isLoading = false
    private var currentPage = 1
    private val photosList = mutableListOf<Photo>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        adapter = PhotosAdapter(photosList, clickListener = { photo ->
            val imageurl = photo.urls.regular
            val imageUser = photo.user.name
            val imageDescription = photo.description

            Log.d(HomeFragment.TAG, "user: ${imageUser}, desc: ${imageDescription}")

            val fullScreen = ImageFullScreen()
            val bundle = Bundle().apply {
                putString("image_url", imageurl)
                putString("image_user", imageUser)
                putString("image_desc", imageDescription)
            }
            fullScreen.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fullScreen)
                .addToBackStack(null)
                .commit()
        }, longClickListener = { photo ->

            val imageUrl = photo.urls.regular
            val imageuser = photo.user.name
            val imageuserid = photo.id
            val imageDesc = photo.description

            Log.d(TAG, "${photo.urls}")

            val dialog = LongPressImage()

            val bundle = Bundle().apply {
                putString("image_url", imageUrl)
                putString("image_user", imageuser)
                putString("image_userid", imageuserid)
                putString("image_description", imageDesc)
            }

            dialog.arguments = bundle
            dialog.show(parentFragmentManager, "PhotoOptionDialog")
        }
        )

        recyclerView.adapter = adapter

        setupScrollListener()
        loadPhotos(currentPage)

        return root
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItem == totalItemCount - 1) {
                    // If not loading and at the end of the list, load more items
                    currentPage++
                    loadPhotos(currentPage)
                    isLoading = true
                }
            }
        })
    }

    private fun loadPhotos(page: Int) {
        val unsplashApi = RetrofitClient.instance.create(UnsplashApi::class.java)
        unsplashApi.searchPhotos("nature", accessKey, page, 10).enqueue(object : Callback<UnsplashResponse> {
            override fun onResponse(call: Call<UnsplashResponse>, response: Response<UnsplashResponse>) {
                if (response.isSuccessful) {
                    val photos = response.body()?.results
                    //val photoUrls = photos?.map { it.urls.regular } ?: listOf()
                    if (photos != null) {
                        photosList.addAll(photos)
                        adapter.notifyDataSetChanged()
                        isLoading = false
                    }

                } else {
                    Toast.makeText(context, "Failed to get photos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UnsplashResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }

    companion object{
        const val TAG = "HomeFragment"
    }


}
