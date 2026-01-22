package com.tezov.tuucho.core.data.repository.image

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import okio.Buffer

internal class ImageLocalFetcher(
    private val path: String,
    private val options: Options,
    private val assetSource: AssetSourceProtocol
) : Fetcher {
    override suspend fun fetch(): SourceFetchResult = assetSource.readImage(path) { content ->
        SourceFetchResult(
            source = ImageSource(
                source = Buffer().apply {
                    writeAll(content.source)
                },
                fileSystem = options.fileSystem
            ),
            mimeType = content.contentType,
            dataSource = DataSource.NETWORK
        )
    }

    class Factory(
        private val assetSource: AssetSourceProtocol,
    ) : Fetcher.Factory<ImageRequest> {
        override fun create(
            data: ImageRequest,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = ImageLocalFetcher(
            path = data.target,
            options = options,
            assetSource = assetSource
        )
    }
}
