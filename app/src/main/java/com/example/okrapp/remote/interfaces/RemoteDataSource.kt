package com.example.okrapp.remote.interfaces

import com.example.okrapp.IOTaskResult
import com.example.okrapp.data.models.PhotoResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {

    val webService: WebService

    @ExperimentalCoroutinesApi
    suspend fun getPhotosByPage(pageNumber: Int): Flow<IOTaskResult<PhotoResponse>>
}