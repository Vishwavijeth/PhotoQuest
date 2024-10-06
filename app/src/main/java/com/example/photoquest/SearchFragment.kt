package com.example.photoquest

import PhotosAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private val accessKey = "OhkF0RF7HgKfar3OMl4G5AierX0q8_tdOjn-rkPODL4"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotosAdapter
    private lateinit var searchEditText: EditText
    private val photosList = mutableListOf<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = root.findViewById(R.id.searchRecyclerView)
        searchEditText = root.findViewById(R.id.searchEditText)

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

            Log.d(SearchFragment.TAG, "user: ${imageUser}, desc: ${imageDescription}")
        }, longClickListener = {photo ->

            val imageUrl = photo.urls.regular
            val imageuser = photo.user.name
            val imageuserid = photo.id
            val imageDesc = photo.description

            Log.d(HomeFragment.TAG, "${photo.urls}")

            val dialogSearchFragment = LongPressImage()

            val bundle = Bundle().apply {
                putString("image_url", imageUrl)
                putString("image_user", imageuser)
                putString("image_userid", imageuserid)
                putString("image_description", imageDesc)
            }

            dialogSearchFragment.arguments = bundle
            dialogSearchFragment.show(parentFragmentManager, "PhotoOptionDialog")

            Log.d(SearchFragment.TAG, "long pressed")
            val dialog = LongPressImage()
            dialog.show(parentFragmentManager, "PhotoOptionDialog")
        })
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    SearchPhotos(query)
                }
            }
        })

        return root
    }

    private fun SearchPhotos(query: String) {
        val unsplashApi = RetrofitClient.instance.create(UnsplashApi::class.java)
        unsplashApi.searchPhotos(query, accessKey, 1, 20).enqueue(object : Callback<UnsplashResponse> {
            override fun onResponse(call: Call<UnsplashResponse>, response: Response<UnsplashResponse>) {
                if (response.isSuccessful) {
                    val photos = response.body()?.results
                    //val photoUrls = photos?.map { it.urls.regular } ?: listOf()
                    photosList.clear()
                    if (photos != null) {
                        photosList.addAll(photos)
                        adapter.notifyDataSetChanged()
                    }

                } else {
                    Toast.makeText(context, "Failed to get photos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UnsplashResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        const val TAG = "SearchFragment"
    }

}