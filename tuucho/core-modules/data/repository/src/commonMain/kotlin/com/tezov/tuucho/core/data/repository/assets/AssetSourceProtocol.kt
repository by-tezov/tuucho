package com.tezov.tuucho.core.data.repository.assets

import android.webkit.MimeTypeMap
import com.tezov.tuucho.core.data.repository.exception.DataException

interface AssetSourceProtocol {

    fun readFile(
        path: String
    ): AssetContent

    fun readImage(
        path: String
    ): AssetContent
}

internal class AssetSource(
    private val reader: AssetReaderProtocol
) : AssetSourceProtocol {

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
        path: String,
    ): AssetContent = reader.read(
        path = "files/$path",
        contentType = resolveFileContentType(path)
    )

    override fun readImage(
        path: String
    ): AssetContent {
        val assetPath = resolveImageAssetPath("files/$path")
        return reader.read(
            path = assetPath,
            contentType = resolveImageFileContentType(path)
        )
    }

    private fun resolveImageAssetPath(
        basePath: String
    ): String {
        val hasExtension = basePath.substringAfterLast('/', "").contains('.')
        if (hasExtension && reader.isExist(basePath)) {
            return basePath
        }
        imageContentTypes.keys.forEach { ext ->
            val candidate = "$basePath.$ext"
            if (reader.isExist(candidate)) {
                return candidate
            }
        }
        if (hasExtension) {
            val baseWithoutExt = basePath.substringBeforeLast('.')
            imageContentTypes.keys.forEach { ext ->
                val candidate = "$baseWithoutExt.$ext"
                if (reader.isExist(candidate)) {
                    return candidate
                }
            }
        }
        throw DataException.Default("resource not found")
    }

    private fun resolveFileContentType(
        path: String
    ): String? {
        val extension = path.substringAfterLast('.', "").lowercase()
        return MimeTypeMap
            .getSingleton()
            .getMimeTypeFromExtension(extension)
    }

    private fun resolveImageFileContentType(
        path: String
    ): String? {
        val extension = path.substringAfterLast('.', "").lowercase()
        return imageContentTypes.entries
            .firstOrNull { extension == it.key }
            ?.value
    }
}
