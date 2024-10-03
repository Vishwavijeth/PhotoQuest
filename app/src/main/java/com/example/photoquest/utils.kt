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
    val urls: Urls
)

data class Urls(
    val regular: String
)
