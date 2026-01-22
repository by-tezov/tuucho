package com.tezov.tuucho.core.data.repository.image.source

import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.target.Target
import com.tezov.tuucho.core.data.repository.exception.DataException
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

internal class ImageLoaderSource(
    private val imageLoader: ImageLoader,
    private val platformContext: PlatformContext
) {
    private fun ProducerScope<ImageResponse>.targetFlow(
        target: String,
        errorMessage: () -> String,
    ) = object : Target {
        override fun onStart(
            placeholder: Image?
        ) {
            placeholder?.let {
                launch { send(ImageResponse.Placeholder(target = target, image = it)) }
            }
        }

        override fun onError(
            error: Image?
        ) {
            close(DataException.Default(errorMessage()))
        }

        override fun onSuccess(
            result: Image
        ) {
            launch {
                send(ImageResponse.Success(target = target, image = result))
                close()
            }
        }
    }

    fun retrieve(
        request: ImageRequest.Remote
    ): Flow<ImageResponse> = callbackFlow {
        val request = coil3.request.ImageRequest
            .Builder(platformContext)
            .data(request)
            .target(
                targetFlow(
                    target = request.url,
                    errorMessage = { "failed to retrieve remote image with url ${request.url}" }
                )
            ).build()
        imageLoader.enqueue(request)
        awaitClose { }
    }

    fun retrieve(
        request: ImageRequest.Local
    ): Flow<ImageResponse> = callbackFlow {
        val request = coil3.request.ImageRequest
            .Builder(platformContext)
            .data(request)
            .target(
                targetFlow(
                    target = request.path,
                    errorMessage = { "failed to retrieve local image with path ${request.path}" }
                )
            ).build()
        imageLoader.enqueue(request)
        awaitClose { }
    }
}
