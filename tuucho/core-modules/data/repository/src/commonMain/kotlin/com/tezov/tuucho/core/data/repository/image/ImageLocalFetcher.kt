package com.tezov.tuucho.core.data.repository.image

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import okio.buffer

internal class ImageLocalFetcher(
    private val path: String,
    private val options: Options,
    private val assetSource: AssetSourceProtocol
) : ImageFetcherProtocol {
    override suspend fun fetch(): SourceFetchResult {
        val content = assetSource.readImage(path)
        return SourceFetchResult(
            source = ImageSource(
                source = content.source.buffer(),
                fileSystem = options.fileSystem
            ),
            mimeType = content.contentType,
            dataSource = DataSource.DISK
        )
    }

    class Factory(
        private val assetSource: AssetSourceProtocol,
    ) : ImageFetcherProtocol.Factory {
        override suspend fun isAvailable(
            request: ImageRequest
        ) = true

        override fun create(
            data: ImageRequest,
            options: Options,
            imageLoader: ImageLoader
        ) = ImageLocalFetcher(
            path = data.target,
            options = options,
            assetSource = assetSource
        )
    }
}
