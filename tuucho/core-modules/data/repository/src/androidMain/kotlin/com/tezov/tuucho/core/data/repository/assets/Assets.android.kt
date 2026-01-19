package com.tezov.tuucho.core.data.repository.assets

import android.content.Context
import android.webkit.MimeTypeMap
import com.tezov.tuucho.core.data.repository.exception.DataException
import io.ktor.http.Headers
import okio.source
import java.net.URLConnection

internal class AssetsAndroid(
    private val context: Context
) : AssetsProtocol {
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
        assetPath = "files/${request.path}",
        contentType = guessContentType(request.path)
    )

    override fun readImage(
        request: AssetsProtocol.Request
    ): AssetsProtocol.Response = try {
        val assetPath = resolveImageAssetPath("files/${request.path}")
        openAsset(
            assetPath = assetPath,
            contentType = imageContentTypes.entries
                .firstOrNull { assetPath.endsWith(".${it.key}") }
                ?.value
                ?: "application/octet-stream"
        )
    } catch (throwable: Throwable) {
        AssetsProtocol.Response.Failure(
            error = throwable
        )
    }

    private fun openAsset(
        assetPath: String,
        contentType: String
    ): AssetsProtocol.Response = try {
        val inputStream = context.assets.open(assetPath)
        val source = inputStream.source()

        val contentLength = runCatching {
            context.assets.openFd(assetPath).length
        }.getOrElse {
            inputStream.available().toLong()
        }

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

    private fun resolveImageAssetPath(
        basePath: String
    ): String {
        val hasExtension = basePath.substringAfterLast('/', "").contains('.')

        if (hasExtension && assetExists(basePath)) {
            return basePath
        }

        imageContentTypes.keys.forEach { ext ->
            val candidate = "$basePath.$ext"
            if (assetExists(candidate)) {
                return candidate
            }
        }

        if (hasExtension) {
            val baseWithoutExt = basePath.substringBeforeLast('.')
            imageContentTypes.keys.forEach { ext ->
                val candidate = "$baseWithoutExt.$ext"
                if (assetExists(candidate)) {
                    return candidate
                }
            }
        }

        throw DataException.Default("resource not found")
    }

    private fun assetExists(
        path: String
    ): Boolean = try {
        context.assets.open(path).close()
        true
    } catch (_: Throwable) {
        false
    }

    private fun guessContentType(
        path: String
    ): String {
        val extension = path.substringAfterLast('.', "").lowercase()
        return MimeTypeMap
            .getSingleton()
            .getMimeTypeFromExtension(extension)
            ?: URLConnection.guessContentTypeFromName(path)
            ?: "application/octet-stream"
    }
}
