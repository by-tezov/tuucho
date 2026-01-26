package com.tezov.tuucho.core.data.repository.assets

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.use
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.UniformTypeIdentifiers.UTType

class AssetReaderIos : AssetReaderProtocol {
    private fun resolveFilePath(
        path: String
    ): String? {
        val (name, ext, subdir) = splitResourcePath("assets/files/$path")
        return NSBundle.mainBundle.pathForResource(name, ext, subdir)
    }

    override fun isExist(
        path: String
    ) = resolveFilePath(path) != null

    override fun <T> read(
        path: String,
        contentType: String?,
        block: (AssetContent) -> T
    ): T {
        val filePath = resolveFilePath(path) ?: error("resource $path not found")
        val size = NSData
            .dataWithContentsOfFile(filePath)
            ?.length
            ?.toLong()
            ?: -1L
        return FileSystem.SYSTEM
            .source(filePath.toPath())
            .use { source ->
                block(
                    AssetContent(
                        source = source,
                        contentType = contentType ?: resolveContentType(path),
                        size = size
                    )
                )
            }
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
