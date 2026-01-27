package com.tezov.tuucho.core.data.repository.assets

interface AssetReaderProtocol {
    fun isExist(
        path: String
    ): Boolean

    fun <T> read(
        path: String,
        contentType: String?,
        block: (AssetContent) -> T
    ): T

    fun read(
        path: String,
        contentType: String?
    ): AssetContent

}
