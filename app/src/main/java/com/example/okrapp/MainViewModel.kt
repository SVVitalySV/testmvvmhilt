package com.example.okrapp

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.okrapp.data.models.Photo
import com.example.okrapp.data.models.PhotoDetails
import com.example.okrapp.data.models.PhotoResponse
import com.example.okrapp.domain.GetPopularPhotosUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(
    private val getPopularPhotosUseCase: GetPopularPhotosUseCase
    ) : ViewModel() {


    val popularPhotosLiveData: MutableLiveData<ViewState<List<Photo>>> by lazy {
        MutableLiveData<ViewState<List<Photo>>>()
    }

    private var currentPageNumber = 1
    private var maximumPageNumber = 2
    private val photoList = ArrayList<Photo>()

    var navigatingFromDetails = false

    @ExperimentalCoroutinesApi
    fun getPhotosNextPage() {

        if (navigatingFromDetails) {
            popularPhotosLiveData.value = ViewState.RenderSuccess(photoList)
            return
        }

        if (currentPageNumber < maximumPageNumber) {
            viewModelScope.launch {
                getViewStateFlowForNetworkCall {
                    getPopularPhotosUseCase.execute(currentPageNumber)
                }.collect {
                    when (it) {
                        is ViewState.Loading -> popularPhotosLiveData.value = it
                        is ViewState.RenderFailure -> popularPhotosLiveData.value = it
                        is ViewState.RenderSuccess<PhotoResponse> -> {
                            currentPageNumber++
                            maximumPageNumber = it.output.totalPages
                            photoList.addAll(it.output.photos)
                            popularPhotosLiveData.value = ViewState.RenderSuccess(photoList)
                        }
                    }
                }
            }
        }
    }

    fun processPhotoDetailsArgument(@NonNull args: Bundle) = flow {
        val photoDetails = args.getParcelable<PhotoDetails>("photoDetails")

        photoDetails?.let {
            emit(ViewState.RenderSuccess(it))
        } ?: run {
            emit(ViewState.RenderFailure(Exception("No Photo Details found")))

        }
    }.asLiveData()

    @ExperimentalCoroutinesApi
    fun onRecyclerViewScrolledToBottom() {
        if (navigatingFromDetails) navigatingFromDetails = false
        getPhotosNextPage()
    }
}