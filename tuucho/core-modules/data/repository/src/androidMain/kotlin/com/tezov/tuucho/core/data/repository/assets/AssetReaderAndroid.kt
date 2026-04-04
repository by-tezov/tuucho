package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileAndroid
import com.tezov.tuucho.core.data.repository.exception.DataException
import okio.source
import okio.use
import java.net.URLConnection

internal class AssetReaderAndroid(
    private val platform: SystemPlatformFileAndroid,
) : AssetReaderProtocol {
    private fun openStream(
        path: String
    ) = platform.context.assets.open(platform.assetPath(path))

    override suspend fun isExist(
        path: String
    ) = runCatching {
        openStream(path).close()
        true
    }.getOrElse { false }

    override suspend fun <T> read(
        path: String,
        contentType: String?,
        block: (AssetContent) -> T
    ): T {
        val assetContent = read(path, contentType)
        return assetContent.source.use {
            block(assetContent)
        }
    }

    override suspend fun read(
        path: String,
        contentType: String?
    ): AssetContent {
        val resourcePath = platform.assetPath(path)
        val inputStream = openStream(resourcePath)
        val source = inputStream.source()
        val size = runCatching { inputStream.available().toLong() }.getOrNull() ?: -1L
        return AssetContent(
            source = source,
            contentType = contentType ?: resolveContentType(path),
            size = size
        )
    }

    private fun resolveContentType(
        path: String
    ) = URLConnection.guessContentTypeFromName(path)
        ?: "application/octet-stream"
}
