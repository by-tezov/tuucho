package com.tezov.tuucho.core.data.repository.image.source

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import okio.Buffer

internal class HttpRemoteFetcher(
    private val url: String,
    private val options: Options,
    private val httpClient: HttpClient
) : Fetcher {
    override suspend fun fetch(): SourceFetchResult {
        val response = httpClient.get(url)
        return SourceFetchResult(
            source = ImageSource(
                source = Buffer().write(response.body<ByteArray>()),
                fileSystem = options.fileSystem
            ),
            mimeType = response.headers["Content-Type"],
            dataSource = DataSource.NETWORK
        )
    }

    class Factory(
        private val httpClient: HttpClient,
        private val config: NetworkModule.Config,
    ) : Fetcher.Factory<ImageRequest.Remote> {
        override fun create(
            data: ImageRequest.Remote,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = HttpRemoteFetcher(
            url = "${config.baseUrl}/${config.version}/${config.imageEndpoint}/${data.url}",
            options = options,
            httpClient = httpClient
        )
    }
}
