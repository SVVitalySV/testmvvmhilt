package com.example.okrapp.remote.network

import com.example.okrapp.IOTaskResult
import com.example.okrapp.data.models.PhotoResponse
import com.example.okrapp.performSafeNetworkApiCall
import com.example.okrapp.remote.interfaces.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitWebService @Inject constructor(private val retrofitClient: FiveHundredPixelsAPI) :
    WebService {

    @ExperimentalCoroutinesApi
    override suspend fun getPhotosByPage(
        pageNumber: Int
    ): Flow<IOTaskResult<PhotoResponse>> =

        performSafeNetworkApiCall("Error Obtaining Photos") {
            retrofitClient.getPopularPhotos(
                page = pageNumber
            )
        }
}