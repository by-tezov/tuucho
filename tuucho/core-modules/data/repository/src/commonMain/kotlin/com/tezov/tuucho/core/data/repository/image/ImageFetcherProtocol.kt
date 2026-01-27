package com.tezov.tuucho.core.data.repository.image

import coil3.fetch.Fetcher

interface ImageFetcherProtocol : Fetcher {

    interface Factory : Fetcher.Factory<ImageRequest> {

        fun isAvailable(request: ImageRequest): Boolean
    }

}
