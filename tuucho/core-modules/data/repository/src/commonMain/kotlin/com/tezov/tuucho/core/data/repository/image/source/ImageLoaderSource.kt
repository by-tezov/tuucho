package com.tezov.tuucho.core.data.repository.image.source

import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.crossfade
import coil3.target.Target
import com.tezov.tuucho.core.data.repository.exception.DataException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class ImageLoaderSource(
    private val imageLoader: ImageLoader,
    private val platformContext: PlatformContext
) {

    suspend fun retrieve(request: ImageRequest.Remote): ImageResponse = suspendCancellableCoroutine { continuation ->
       val request = coil3.request.ImageRequest.Builder(platformContext)
            .data(request)
            .crossfade(true)
            .target(
                object : Target {
                    override fun onStart(placeholder: Image?) {}
                    override fun onError(error: Image?) {
                        continuation.resumeWithException(
                            DataException.Default("failed to retrieve remote image with url ${request.url}")
                        )
                    }
                    override fun onSuccess(result: Image) {
                        continuation.resume(
                            ImageResponse(
                                url = request.url,
                                image = result
                            )
                        )
                    }
                }
            )
            .build()
        imageLoader.enqueue(request)
    }

}
