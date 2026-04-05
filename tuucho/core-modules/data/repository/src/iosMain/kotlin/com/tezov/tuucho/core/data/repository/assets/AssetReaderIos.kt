package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileIos
import com.tezov.tuucho.core.data.repository.exception.DataException
import okio.Path.Companion.toPath
import okio.use
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.UniformTypeIdentifiers.UTType

class AssetReaderIos(
    private val platform: SystemPlatformFileIos
) : AssetReaderProtocol {
    override suspend fun isExist(
        path: String
    ) = platform.assetPath(path) != null

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
        val filePath = platform.assetPath(path) ?: throw DataException.Default("asset resource $path not found")
        val size = NSData.dataWithContentsOfFile(filePath)?.length?.toLong() ?: -1L
        return AssetContent(
            source = platform.fileSystem().source(filePath.toPath()),
            contentType = contentType ?: resolveContentType(path),
            size = size
        )
    }

    private fun resolveContentType(
        path: String
    ): String {
        val extension = path.substringAfterLast('.', "").lowercase()
        if (extension.isNotEmpty()) {
            val utType = UTType.typeWithFilenameExtension(extension)
            val mime = utType?.preferredMIMEType
            if (mime != null) return mime
        }
        return "application/octet-stream"
    }
}
