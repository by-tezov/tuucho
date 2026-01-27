package com.tezov.tuucho.core.data.repository.image

import coil3.ImageLoader
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.HttpClient

internal class ImageRemoteFetcher(
    private val url: String,
    private val options: Options,
    private val httpClient: HttpClient,
    private val diskCache: ImageDiskCacheProtocol
) : ImageFetcherProtocol {

    override suspend fun fetch(): SourceFetchResult {
        val diskCacheKey = options.diskCacheKey ?: throw DataException.Default("diskCacheKey is null")
        diskCache.retrieve(
            diskCacheKey = diskCacheKey,
        )?.let { return it }
        val response = httpClient.getImage(url)
        return diskCache.saveAndRetrieve(
            diskCacheKey = diskCacheKey,
            response = response
        )
    }

    class Factory(
        private val config: NetworkModule.Config,
        private val httpClient: HttpClient,
        private val diskCache: ImageDiskCacheProtocol
    ) : ImageFetcherProtocol.Factory {

        override fun isAvailable(request: ImageRequest): Boolean {
            val diskCacheKey = request.cacheKey
            return diskCache.isAvailable(diskCacheKey)
        }

        override fun create(
            data: ImageRequest,
            options: Options,
            imageLoader: ImageLoader
        ) = ImageRemoteFetcher(
            url = "${config.baseUrl}/${config.version}/${config.imageEndpoint}/${data.target}",
            options = options,
            httpClient = httpClient,
            diskCache = diskCache
        )
    }
}
