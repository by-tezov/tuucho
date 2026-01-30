package com.tezov.tuucho.core.data.repository.database.entity

import com.tezov.tuucho.core.data.repository.database.table.ImageEntry

internal data class ImageEntity(
    val primaryKey: Long? = null,
    val cacheKey: String,
    val mimeType: String
)

internal fun ImageEntry.toEntity() = ImageEntity(
    primaryKey = primaryKey,
    cacheKey = cacheKey,
    mimeType = mimeType
)
