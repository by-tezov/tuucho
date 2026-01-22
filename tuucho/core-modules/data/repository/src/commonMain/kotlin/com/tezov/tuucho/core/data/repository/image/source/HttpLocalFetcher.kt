package com.tezov.tuucho.core.data.repository.image.source

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import okio.buffer

internal class HttpLocalFetcher(
    private val path: String,
    private val options: Options,
    private val assetSource: AssetSourceProtocol
) : Fetcher {
    override suspend fun fetch(): SourceFetchResult {
        val content = assetSource.readImage(
            path = path
        )
        return SourceFetchResult(
            source = ImageSource(
                source = content.source.buffer(),
                fileSystem = options.fileSystem
            ),
            mimeType = content.contentType,
            dataSource = DataSource.NETWORK
        )
    }

    class Factory(
        private val assetSource: AssetSourceProtocol,
    ) : Fetcher.Factory<ImageRequest.Local> {
        override fun create(
            data: ImageRequest.Local,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = HttpLocalFetcher(
            path = data.path,
            options = options,
            assetSource = assetSource
        )
    }
}
