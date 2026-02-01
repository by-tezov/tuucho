package com.tezov.tuucho.core.data.repository.assets

interface AssetReaderProtocol {
    suspend fun isExist(
        path: String
    ): Boolean

    suspend fun read(
        path: String,
        contentType: String?
    ): AssetContent

    suspend fun <T> read(
        path: String,
        contentType: String?,
        block: (AssetContent) -> T
    ): T
}
