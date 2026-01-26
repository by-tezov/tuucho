package com.tezov.tuucho.core.data.repository.image

import coil3.ImageLoader
import coil3.PlatformContext
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch

internal class ImageLoaderSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageLoader: ImageLoader,
    private val platformContext: PlatformContext,
    private val imageFetchers: ImageFetcherRegistryProtocol
) {
    fun retrieve(
        requests: List<ImageRequest>
    ): Flow<ImageResponse> = callbackFlow {
        val counter = AtomicInt(0)
        val counterEnd = requests.size
        requests.forEach { enqueue(it, counter, counterEnd) }
        awaitClose { } // TODO timeout
    }

    private fun ProducerScope<ImageResponse>.enqueue(
        request: ImageRequest,
        counter: AtomicInt,
        counterEnd: Int
    ) {
        val coilRequest = coil3.request.ImageRequest
            .Builder(platformContext)
            .fetcherCoroutineContext(coroutineScopes.image.context)
            .decoderCoroutineContext(coroutineScopes.image.context)
            .fetcherFactory(imageFetchers.get(request.command))
            .data(request)
            .diskCacheKey(request.cacheKey)
            .target(
                onSuccess = { image ->
                    send(
                        response = ImageResponse(target = request.target, tag = request.tag, image = image),
                        counter = counter,
                        counterEnd = counterEnd,
                    )
                },
                onError = {
                    sendFailure(DataException.Default("failed to retrieve image $request"))
                }
            ).build()
        imageLoader.enqueue(coilRequest)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun ProducerScope<ImageResponse>.send(
        response: ImageResponse,
        counter: AtomicInt,
        counterEnd: Int
    ) {
        if (!isClosedForSend) {
            trySend(response)
                .onFailure { close() }
                .onSuccess {
                    if (counter.incrementAndFetch() >= counterEnd) {
                        close()
                    }
                }
        }
    }

    private fun ProducerScope<ImageResponse>.sendFailure(
        throwable: Throwable
    ) {
        close(throwable)
    }
}
