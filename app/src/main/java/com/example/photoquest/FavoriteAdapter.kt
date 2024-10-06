package com.example.photoquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteAdapter(private val favoriteList: List<Favorite>): RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>()
{
    var currentFormat = "Compact"

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.favoriteImageView)
        val userName: TextView = view.findViewById(R.id.favoriteUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]
        holder.userName.text = favorite.userName
        Glide.with(holder.itemView.context)
            .load(favorite.imageUrl)
            .into(holder.imageView)

        val layoutParams = holder.imageView.layoutParams
        when (currentFormat) {
            "Wide" -> {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = 500
                layoutParams.width = 1000
            }
            "Compact" -> {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = 300
                layoutParams.width = 400
            }
        }
        holder.imageView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }
}