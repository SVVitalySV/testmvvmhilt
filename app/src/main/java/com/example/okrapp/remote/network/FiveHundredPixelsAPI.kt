package com.example.okrapp.remote.network

import com.example.okrapp.BuildConfig
import com.example.okrapp.data.models.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface FiveHundredPixelsAPI {

    @GET("/v1/photos?image_size=5,6")
    suspend fun getPopularPhotos(
        @Query("consumer_key") key: String = BuildConfig.API_KEY,
        @Query("feature") feature: String = "popular",
        @Query("page") page: Int
    ): Response<PhotoResponse>
}