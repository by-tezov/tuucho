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

class AssetsIos : AssetsProtocol {
    companion object {
        private val imageContentTypes = mapOf(
            "svg" to "image/svg+xml",
            "png" to "image/png",
            "jpg" to "image/jpeg",
            "jpeg" to "image/jpeg",
            "gif" to "image/gif",
            "webp" to "image/webp"
        )
    }

    override fun readFile(
        request: AssetsProtocol.Request
    ): AssetsProtocol.Response = openAsset(
        resourcePath = "assets/files/${request.path}",
        contentType = guessContentType(request.path)
    )

    override fun readImage(
        request: AssetsProtocol.Request
    ): AssetsProtocol.Response = try {
        val resourcePath = resolveImageResourcePath("assets/files/${request.path}")
        openAsset(
            resourcePath = resourcePath,
            contentType = imageContentTypes.entries
                .firstOrNull { resourcePath.endsWith(".${it.key}") }
                ?.value
                ?: "application/octet-stream"
        )
    } catch (throwable: Throwable) {
        AssetsProtocol.Response.Failure(
            error = throwable
        )
    }

    private fun openAsset(
        resourcePath: String,
        contentType: String
    ): AssetsProtocol.Response = try {
        val (name, ext, subdir) = splitResourcePath(resourcePath)
        val filePath = NSBundle.mainBundle
            .pathForResource(name, ext, subdir)
            ?: error("resource not found")
        val source: Source = FileSystem.SYSTEM.source(filePath.toPath())
        val contentLength = NSData
            .dataWithContentsOfFile(filePath)
            ?.length
            ?.toLong()
            ?: 0L
        AssetsProtocol.Response.Success(
            source = source,
            headers = Headers.build {
                this["Content-Type"] = contentType
                this["Content-Length"] = contentLength.toString()
            }
        )
    } catch (throwable: Throwable) {
        AssetsProtocol.Response.Failure(
            error = throwable
        )
    }

    private fun resolveImageResourcePath(
        basePath: String
    ): String {
        val hasExtension = basePath.substringAfterLast('/', "").contains('.')
        if (hasExtension && resourceExists(basePath)) {
            return basePath
        }
        imageContentTypes.keys.forEach { ext ->
            val candidate = "$basePath.$ext"
            if (resourceExists(candidate)) {
                return candidate
            }
        }
        if (hasExtension) {
            val baseWithoutExt = basePath.substringBeforeLast('.')
            imageContentTypes.keys.forEach { ext ->
                val candidate = "$baseWithoutExt.$ext"
                if (resourceExists(candidate)) {
                    return candidate
                }
            }
        }
        throw DataException.Default("resource not found")
    }

    private fun resourceExists(
        resourcePath: String
    ): Boolean {
        val (name, ext, subdir) = splitResourcePath(resourcePath)
        return NSBundle.mainBundle.pathForResource(name, ext, subdir) != null
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

    private fun guessContentType(
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
