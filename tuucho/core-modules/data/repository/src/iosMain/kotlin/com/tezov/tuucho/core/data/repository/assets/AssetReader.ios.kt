package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository.exception.DataException
import io.ktor.http.Headers
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.length
import platform.UniformTypeIdentifiers.UTType

class AssetReaderIos : AssetReaderProtocol {

    override fun isExist(path: String): Boolean  {
        val (name, ext, subdir) = splitResourcePath(resourcePath)
        return NSBundle.mainBundle.pathForResource(name, ext, subdir) != null
    }

    override fun read(
        path: String,
        contentType: String?
    ): AssetContent {
        val (name, ext, subdir) = splitResourcePath(resourcePath)
        val filePath = NSBundle.mainBundle
            .pathForResource(name, ext, subdir)
            ?: error("resource $path not found")
        val source: Source = FileSystem.SYSTEM.source(filePath.toPath())
        val contentLength = NSData
            .dataWithContentsOfFile(filePath)
            ?.length
            ?.toLong()
            ?: 0L
        return AssetContent(
            source = source,
            contentType = contentType ?: resolveContentType(path),
            size = contentLength,
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
