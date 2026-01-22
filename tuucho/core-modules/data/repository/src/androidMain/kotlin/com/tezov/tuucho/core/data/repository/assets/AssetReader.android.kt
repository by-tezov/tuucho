package com.tezov.tuucho.core.data.repository.assets

import android.content.Context
import okio.source
import java.net.URLConnection

internal class AssetReaderAndroid(
    private val context: Context
) : AssetReaderProtocol {

    override fun isExist(path: String): Boolean = runCatching {
        context.assets.open(path).close()
        true
    }.getOrElse { false }

    override fun read(
        path: String,
        contentType: String?
    ): AssetContent {
        val inputStream = context.assets.open(path)
        val source = inputStream.source()
        val contentLength = runCatching {
            context.assets.openFd(path).length
        }.getOrElse {
            inputStream.available().toLong()
        }
        return AssetContent(
            source = source,
            contentType = contentType ?: resolveContentType(path),
            size = contentLength,
        )
    }

    private fun resolveContentType(
        path: String
    ) = URLConnection.guessContentTypeFromName(path)
        ?: "application/octet-stream"
}
