package com.tezov.tuucho.core.data.repository.assets

import okio.use

interface AssetReaderProtocol {
    fun isExist(
        path: String
    ): Boolean

    fun read(
        path: String,
        contentType: String?
    ): AssetContent

    fun <T> read(
        path: String,
        contentType: String?,
        block: (AssetContent) -> T
    ): T {
        val assetContent = read(path, contentType)
        return assetContent.source.use {
            block(assetContent.copy(source = it))
        }
    }
}
