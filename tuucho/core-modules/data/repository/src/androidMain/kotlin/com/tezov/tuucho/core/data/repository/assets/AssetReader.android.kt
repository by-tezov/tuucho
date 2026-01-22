package com.tezov.tuucho.core.data.repository.assets

import android.content.Context
import okio.source
import java.net.URLConnection

internal class AssetReaderAndroid(
    private val context: Context
) : AssetReaderProtocol {
    private fun openStream(
        path: String
    ) = context.assets.open("files/$path")

    private fun openDescriptor(
        path: String
    ) = context.assets.openFd("files/$path")

    override fun isExist(
        path: String
    ): Boolean = runCatching {
        openStream(path).close()
        true
    }.getOrElse { false }

    override fun <T> read(
        path: String,
        contentType: String?,
        block: (AssetContent) -> T
    ): T {
        val inputStream = openStream(path)
        val source = inputStream.source()
        val size = runCatching {
            openDescriptor(path).use { it.length }
        }.getOrElse { -1L }
        return source.use {
            block(
                AssetContent(
                    source = it,
                    contentType = contentType ?: resolveContentType(path),
                    size = size
                )
            )
        }
    }

    private fun resolveContentType(
        path: String
    ) = URLConnection.guessContentTypeFromName(path)
        ?: "application/octet-stream"
}
