package com.example.okrapp.remote

import com.example.okrapp.IOTaskResult
import com.example.okrapp.data.models.PhotoResponse
import com.example.okrapp.remote.interfaces.RemoteDataSource
import com.example.okrapp.remote.interfaces.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FHPRepository @Inject constructor(override val remoteDataSource: RemoteDataSource) :
    Repository {

    @ExperimentalCoroutinesApi
    override suspend fun getPhotosByPage(pageNumber: Int): Flow<IOTaskResult<PhotoResponse>> =
        remoteDataSource.getPhotosByPage(pageNumber)
}