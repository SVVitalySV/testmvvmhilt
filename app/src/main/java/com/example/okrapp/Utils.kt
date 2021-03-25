package com.example.okrapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.BindingAdapter
import com.example.okrapp.data.models.Photo
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.Response
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

typealias NetworkAPIInvoke<T> = suspend () -> Response<T>

typealias ListItemClickListener<T> = (T) -> Unit

sealed class IOTaskResult<out DTO : Any> {
    data class OnSuccess<out DTO : Any>(val data: DTO) : IOTaskResult<DTO>()
    data class OnFailed(val throwable: Throwable) : IOTaskResult<Nothing>()
}

@ExperimentalCoroutinesApi
suspend fun <T : Any> performSafeNetworkApiCall(
    messageInCaseOfError: String = "Network error",
    allowRetries: Boolean = true,
    numberOfRetries: Int = 2,
    networkApiCall: NetworkAPIInvoke<T>
): Flow<IOTaskResult<T>> {
    var delayDuration = 1000L
    val delayFactor = 2
    return flow {
        val response = networkApiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(IOTaskResult.OnSuccess(it))
            }
                ?: emit(IOTaskResult.OnFailed(IOException("API call successful but empty response body")))
            return@flow
        }
        emit(
            IOTaskResult.OnFailed(
                IOException(
                    "API call failed with error - ${response.errorBody()
                        ?.string() ?: messageInCaseOfError}"
                )
            )
        )
        return@flow
    }.catch { e ->
        emit(IOTaskResult.OnFailed(IOException("Exception during network API call: ${e.message}")))
        return@catch
    }.retryWhen { cause, attempt ->
        if (!allowRetries || attempt > numberOfRetries || cause !is IOException) return@retryWhen false
        delay(delayDuration)
        delayDuration *= delayFactor
        return@retryWhen true
    }.flowOn(Dispatchers.IO)
}

fun ImageView.loadUrl(
    @NonNull url: String,
    placeholder: Drawable = this.context.getDrawable(R.drawable.ic_launcher_foreground)!!,
    error: Drawable = this.context.getDrawable(R.drawable.ic_launcher_background)!!
) {
    Picasso.get()
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String) {
    view.loadUrl(url)
}

sealed class ViewState<out T : Any> {

    data class Loading(val isLoading: Boolean) : ViewState<Nothing>()

    data class RenderSuccess<out T : Any>(val output: T) : ViewState<T>()

    data class RenderFailure(val throwable: Throwable) : ViewState<Nothing>()
}

fun Context.showToast(@NonNull message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Photo.getFormattedExifData() = StringBuilder().apply {

    append(if (camera != null && camera.isBlank()) "Unknown Camera" else camera)
    append(" + ")
    append(if (lens != null && lens.isBlank()) "Unknown Lens" else lens)
    append(" | ")
    append(if (focalLength != null && focalLength.isBlank()) "0mm" else focalLength + "mm")
    appendln()
    append(if (aperture != null && aperture.isBlank()) "f0" else "f/$aperture")
    append(" | ")
    append(if (shutterSpeed != null && shutterSpeed.isBlank()) "0s" else shutterSpeed + "s")
    append(" | ")
    append(if (iso != null && iso.isBlank()) "ISO0" else "ISO$iso")
}.run {
    toString()
}

fun Photo.durationPosted(): String {

    val timeCreatedAt =
        OffsetDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime()
    val duration = Duration.between(timeCreatedAt, LocalDateTime.now())

    return when {
        duration.toDays() == 1L -> {
            "${duration.toDays()} year"
        }
        duration.toDays() > 1 -> {
            "${duration.toDays()} years"
        }
        duration.toHours() == 1L -> {
            "${duration.toHours()} hour"
        }
        duration.toHours() > 1 -> {
            "${duration.toHours()} hours"
        }
        duration.toMinutes() == 1L -> {
            "${duration.toDays()} minute"
        }
        duration.toMinutes() > 1 -> {
            "${duration.toDays()} minutes"
        }
        else -> {
            "Less than a minute"
        }
    }.run {
        "$this ago"
    }
}

@ExperimentalCoroutinesApi
suspend fun <T : Any> getViewStateFlowForNetworkCall(ioOperation: suspend () -> Flow<IOTaskResult<T>>) =
    flow {
        emit(ViewState.Loading(true))
        ioOperation().map {
            when (it) {
                is IOTaskResult.OnSuccess -> ViewState.RenderSuccess(it.data)
                is IOTaskResult.OnFailed -> ViewState.RenderFailure(it.throwable)
            }
        }.collect {
            emit(it)
        }
        emit(ViewState.Loading(false))
    }.flowOn(Dispatchers.IO)