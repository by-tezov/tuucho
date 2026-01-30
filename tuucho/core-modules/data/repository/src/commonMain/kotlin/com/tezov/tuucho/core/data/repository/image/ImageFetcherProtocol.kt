package com.tezov.tuucho.core.data.repository.image

import coil3.fetch.Fetcher

interface ImageFetcherProtocol : Fetcher {
    interface Factory : Fetcher.Factory<ImageRequest> {
        val command: String

        suspend fun isAvailable(
            request: ImageRequest
        ): Boolean
    }
}
