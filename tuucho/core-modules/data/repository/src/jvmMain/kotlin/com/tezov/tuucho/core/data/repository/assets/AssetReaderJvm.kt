package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileJvm
import okio.source
import okio.use
import java.net.URLConnection

internal class AssetReaderJvm(
    private val platform: SystemPlatformFileJvm,
) : AssetReaderProtocol {
    override suspend fun isExist(
        path: String
    ) = platform.classLoader().getResource(platform.assetPath(path)) != null

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
        val inputStream = platform.classLoader().getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Asset not found: $path")
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
