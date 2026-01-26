package com.tezov.tuucho.core.data.repository.assets

import coil3.annotation.InternalCoilApi
import coil3.util.MimeTypeMap
import com.tezov.tuucho.core.data.repository.exception.DataException
import io.ktor.http.ContentType
import io.ktor.http.defaultForFileExtension

interface AssetSourceProtocol {
    fun <T> readFile(
        path: String,
        block: (AssetContent) -> T
    ): T

    fun <T> readImage(
        path: String,
        block: (AssetContent) -> T
    ): T
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

    override fun <T> readFile(
        path: String,
        block: (AssetContent) -> T
    ): T = reader.read(
        path = path,
        contentType = resolveFileContentType(path),
        block = block
    )

    override fun <T> readImage(
        path: String,
        block: (AssetContent) -> T
    ): T {
        val assetPath = resolveImageAssetPath(path)
        return reader.read(
            path = assetPath,
            contentType = resolveImageFileContentType(assetPath),
            block = block
        )
    }

    private fun resolveImageAssetPath(
        path: String
    ): String {
        val hasExtension = path.substringAfterLast('/', "").contains('.')
        if (hasExtension && reader.isExist(path)) {
            return path
        }
        imageContentTypes.keys.forEach { ext ->
            val candidatePath = "$path.$ext"
            if (reader.isExist(candidatePath)) {
                return candidatePath
            }
        }
        if (hasExtension) {
            val basePathWithoutExt = path.substringBeforeLast('.')
            imageContentTypes.keys.forEach { ext ->
                val candidatePath = "$basePathWithoutExt.$ext"
                if (reader.isExist(candidatePath)) {
                    return candidatePath
                }
            }
        }
        throw DataException.Default("resource not found")
    }

    private fun resolveFileContentType(
        path: String
    ): String? {
        val extension = path.substringAfterLast('.', "").lowercase()
        return ContentType
            .defaultForFileExtension(extension)
            .takeIf { it != ContentType.Application.OctetStream }
            ?.toString()
    }

    @OptIn(InternalCoilApi::class)
    private fun resolveImageFileContentType(
        path: String
    ): String? {
        val extension = path.substringAfterLast('.', "").lowercase()
        return MimeTypeMap.getMimeTypeFromExtension(extension)
    }
}
