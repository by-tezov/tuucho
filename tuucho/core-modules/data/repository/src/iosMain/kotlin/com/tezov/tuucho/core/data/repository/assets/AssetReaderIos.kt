package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import com.tezov.tuucho.core.data.repository.exception.DataException
import okio.Path.Companion.toPath
import okio.use
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.UniformTypeIdentifiers.UTType

class AssetReaderIos(
    private val platform: SystemPlatformFileProtocol
) : AssetReaderProtocol {
    private fun assetPath(
        path: String
    ): String? {
        val (name, ext, subdir) = splitResourcePath("assets/files/$path")
        return NSBundle.mainBundle.pathForResource(name, ext, subdir)
    }

    override suspend fun isExist(
        path: String
    ) = assetPath(path) != null

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
        val filePath = assetPath(path) ?: throw DataException.Default("asset resource $path not found")
        val size = NSData.dataWithContentsOfFile(filePath)?.length?.toLong() ?: -1L
        return AssetContent(
            source = platform.fileSystem().source(filePath.toPath()),
            contentType = contentType ?: resolveContentType(path),
            size = size
        )
    }

    private fun splitResourcePath(
        path: String
    ): Triple<String, String, String?> {
        val parts = path.split("/")
        val filename = parts.last()
        val name = filename.substringBeforeLast(".")
        val ext = filename.substringAfterLast(".", "")
        val subdir = parts.dropLast(1).joinToString("/").ifEmpty { null }
        return Triple(name, ext, subdir)
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
