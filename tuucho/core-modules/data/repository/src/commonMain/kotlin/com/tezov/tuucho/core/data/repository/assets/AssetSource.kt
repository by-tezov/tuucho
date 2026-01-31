package com.tezov.tuucho.core.data.repository.assets

import coil3.annotation.InternalCoilApi
import coil3.util.MimeTypeMap
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import io.ktor.http.ContentType
import io.ktor.http.defaultForFileExtension

interface AssetSourceProtocol {
    suspend fun <T> readFile(
        path: String,
        block: (AssetContent) -> T
    ): T

    suspend fun readFile(
        path: String,
    ): AssetContent

    suspend fun <T> readImage(
        path: String,
        block: (AssetContent) -> T
    ): T

    suspend fun readImage(
        path: String,
    ): AssetContent
}

internal class AssetSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val reader: AssetReaderProtocol,
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

    override suspend fun <T> readFile(
        path: String,
        block: (AssetContent) -> T
    ): T = coroutineScopes.io.withContext {
        reader.read(
            path = path,
            contentType = resolveFileContentType(path),
            block = block
        )
    }

    override suspend fun readFile(
        path: String,
    ) = coroutineScopes.io.withContext {
        reader.read(
            path = path,
            contentType = resolveFileContentType(path),
        )
    }

    override suspend fun readImage(
        path: String,
    ) = coroutineScopes.io.withContext {
        val assetPath = resolveImageAssetPath(path)
        reader.read(
            path = assetPath,
            contentType = resolveImageFileContentType(assetPath),
        )
    }

    override suspend fun <T> readImage(
        path: String,
        block: (AssetContent) -> T
    ) = coroutineScopes.io.withContext {
        val assetPath = resolveImageAssetPath(path)
        reader.read(
            path = assetPath,
            contentType = resolveImageFileContentType(assetPath),
            block = block
        )
    }

    private suspend fun resolveImageAssetPath(
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
