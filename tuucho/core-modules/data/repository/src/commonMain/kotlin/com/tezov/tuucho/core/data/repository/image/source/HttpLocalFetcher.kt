package com.tezov.tuucho.core.data.repository.image.source

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import okio.buffer

internal class HttpLocalFetcher(
    private val path: String,
    private val options: Options,
    private val assets: AssetsProtocol
) : Fetcher {
    override suspend fun fetch(): SourceFetchResult {
        val response = assets.readImage(
            AssetsProtocol.Request(path = path)
        )
        if (response is AssetsProtocol.Response.Failure) {
            throw response.error
        }
        val source = (response as AssetsProtocol.Response.Success).source
        return SourceFetchResult(
            source = ImageSource(
                source = source.buffer(),
                fileSystem = options.fileSystem
            ),
            mimeType = response.headers["Content-Type"],
            dataSource = DataSource.NETWORK
        )
    }

    class Factory(
        private val assets: AssetsProtocol,
    ) : Fetcher.Factory<ImageRequest.Local> {
        override fun create(
            data: ImageRequest.Local,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = HttpLocalFetcher(
            path = data.path,
            options = options,
            assets = assets
        )
    }
}
