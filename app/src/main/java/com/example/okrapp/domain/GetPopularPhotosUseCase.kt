package com.example.okrapp.domain

import com.example.okrapp.IOTaskResult
import com.example.okrapp.data.models.PhotoResponse
import com.example.okrapp.remote.interfaces.Repository
import com.example.okrapp.remote.interfaces.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPopularPhotosUseCase @Inject constructor(override val repository: Repository) :
    UseCase<Int, PhotoResponse> {

    @ExperimentalCoroutinesApi
    override suspend fun execute(input: Int): Flow<IOTaskResult<PhotoResponse>> =
        repository.getPhotosByPage(input)
}