package com.tezov.tuucho.core.data.repository.assets

interface AssetReaderProtocol {
    fun isExist(
        path: String
    ): Boolean

    fun read(
        path: String,
        contentType: String?
    ): AssetContent
}
