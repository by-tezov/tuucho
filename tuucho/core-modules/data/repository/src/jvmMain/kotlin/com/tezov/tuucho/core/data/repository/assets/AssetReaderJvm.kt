package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import okio.Path
import okio.Path.Companion.toPath
import okio.use
import java.net.URLConnection

internal class AssetReaderJvm(
    private val platform: SystemPlatformFileProtocol,
) : AssetReaderProtocol {
    private fun assetPath(
        path: String
    ): Path = "assets".toPath().resolve(path.toPath())

    override suspend fun isExist(
        path: String
    ): Boolean = runCatching {
        platform.fileSystem().exists(assetPath(path))
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
        val filePath = assetPath(path)
        val source = platform.fileSystem().source(filePath)
        val size = runCatching { platform.fileSystem().metadata(filePath).size }.getOrNull() ?: -1L
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
