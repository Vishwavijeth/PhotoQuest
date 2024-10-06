package com.example.photoquest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.unsplash.com/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

interface UnsplashApi {
    @GET("search/photos")
    fun searchPhotos(
        @Query("query") query: String,
        @Query("client_id") clientId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<UnsplashResponse>
}

data class UnsplashResponse(
    val results: List<Photo>
)

data class Photo(
    val id: String,
    val description: String?,
    val urls: Urls,
    val user: User,
    val isFavorite: Boolean = false
)

data class User(
    val name: String
)

data class Urls(
    val regular: String
)

data class Favorite(
    val userId: String = "",
    val imageUrl: String = "",
    val userName: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
){
    constructor() : this("", "", "", "", System.currentTimeMillis())
}
