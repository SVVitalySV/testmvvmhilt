package com.example.okrapp.viewmodel.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.okrapp.IOTaskResult
import com.example.okrapp.MainViewModel
import com.example.okrapp.TestCoroutineRule
import com.example.okrapp.ViewState
import com.example.okrapp.data.models.Photo
import com.example.okrapp.data.models.PhotoResponse
import com.example.okrapp.domain.GetPopularPhotosUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList

@ExperimentalCoroutinesApi
class MainViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = TestCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val fakeSuccessFlow = flow {
        emit(IOTaskResult.OnSuccess(mockPhotoResponse))
    }

    private val fakeFailureFlow = flow {
        emit(IOTaskResult.OnFailed(mockException))
    }

    @RelaxedMockK
    private lateinit var viewStateObserver: Observer<ViewState<List<Photo>>>

    @RelaxedMockK
    private lateinit var mockPhotoResponse: PhotoResponse

    @RelaxedMockK
    private lateinit var mockException: Exception

    @RelaxedMockK
    private lateinit var mockUseCase: GetPopularPhotosUseCase

    private val viewModel: MainViewModel by lazy {
        MainViewModel(mockUseCase)
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { mockException.message } returns "Test Exception"
    }

    @Test
    fun `load first page of Photos when getPopular photos is called for the first time`() {

        runBlockingTest {

            coEvery { mockUseCase.execute(1) } returns fakeSuccessFlow

            viewModel.popularPhotosLiveData.observeForever(viewStateObserver)
            viewModel.getPhotosNextPage()

            verifyOrder {
                viewStateObserver.onChanged(ViewState.Loading(true))
                viewStateObserver.onChanged(ViewState.RenderSuccess(anyList()))
                viewStateObserver.onChanged(ViewState.Loading(false))
            }
        }
    }

    @Test
    fun `when load photos service throws network failure then ViewState renders failure`() {

        runBlockingTest {

            coEvery { mockUseCase.execute(1) } returns fakeFailureFlow

            viewModel.popularPhotosLiveData.observeForever(viewStateObserver)
            viewModel.getPhotosNextPage()

            verifyOrder {
                viewStateObserver.onChanged(ViewState.Loading(true))
                viewStateObserver.onChanged(ViewState.RenderFailure(mockException))
                viewStateObserver.onChanged(ViewState.Loading(false))
            }
        }
    }

    @Test
    fun `when list scrolled to the bottom then next page of photos is called`() {

        runBlockingTest {

            coEvery { mockUseCase.execute(1) } returns fakeSuccessFlow

            viewModel.popularPhotosLiveData.observeForever(viewStateObserver)
            viewModel.onRecyclerViewScrolledToBottom()

            verifyOrder {
                viewStateObserver.onChanged(ViewState.Loading(true))
                viewStateObserver.onChanged(ViewState.RenderSuccess(anyList()))
                viewStateObserver.onChanged(ViewState.Loading(false))
            }
        }
    }

    @Test
    fun `when list scrolled to the bottom and error in service call error propagated to view`() {

        runBlockingTest {

            coEvery { mockUseCase.execute(1) } returns fakeFailureFlow

            viewModel.popularPhotosLiveData.observeForever(viewStateObserver)
            viewModel.onRecyclerViewScrolledToBottom()

            verifyOrder {
                viewStateObserver.onChanged(ViewState.Loading(true))
                viewStateObserver.onChanged(ViewState.RenderFailure(mockException))
                viewStateObserver.onChanged(ViewState.Loading(false))
            }
        }
    }
}