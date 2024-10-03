package com.example.photoquest

import PhotosAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private val accessKey = "OhkF0RF7HgKfar3OMl4G5AierX0q8_tdOjn-rkPODL4"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotosAdapter
    private var isLoading = false
    private var currentPage = 1
    private val photosList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        adapter = PhotosAdapter(photosList) { position ->
            showPopupMenu(position)
        }
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
                    val photoUrls = photos?.map { it.urls.regular } ?: listOf()
                    photosList.addAll(photoUrls)
                    adapter.notifyDataSetChanged()
                    isLoading = false
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

    private fun showPopupMenu(position: Int) {
        // Same popup menu logic as before
    }
}
