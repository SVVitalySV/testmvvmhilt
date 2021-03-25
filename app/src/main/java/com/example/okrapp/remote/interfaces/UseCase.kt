package com.example.okrapp.remote.interfaces

import com.example.okrapp.IOTaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface UseCase<in I : Any, out O : Any> {

    val repository: Repository

    @ExperimentalCoroutinesApi
    suspend fun execute(input: I): Flow<IOTaskResult<O>>
}