package com.tezov.tuucho.core.data.repository.image

import coil3.ImageLoader
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.data.repository.exception.DataException
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal class ImageRemoteFetcher(
    private val url: String,
    private val options: Options,
    private val httpClient: HttpClient,
    private val diskCache: ImageDiskCacheProtocol
) : Fetcher {

    override suspend fun fetch(): SourceFetchResult {
        val diskCacheKey = options.diskCacheKey ?: throw DataException.Default("diskCacheKey is null")
        diskCache.retrieve(
            diskCacheKey = diskCacheKey,
        )?.let {
            return it
        }
        val response = httpClient.get(url)
        return diskCache.saveAndRetrieve(
            diskCacheKey = diskCacheKey,
            response = response
        )
    }

    class Factory(
        private val config: NetworkModule.Config,
        private val httpClient: HttpClient,
        private val diskCache: ImageDiskCacheProtocol
    ) : Fetcher.Factory<ImageRequest> {
        override fun create(
            data: ImageRequest,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = ImageRemoteFetcher(
            url = "${config.baseUrl}/${config.version}/${config.imageEndpoint}/${data.target}",
            options = options,
            httpClient = httpClient,
            diskCache = diskCache
        )
    }
}
