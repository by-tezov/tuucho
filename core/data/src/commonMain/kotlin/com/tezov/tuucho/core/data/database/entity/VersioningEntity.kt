package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.table.VersioningEntry
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import kotlin.time.Instant

data class VersioningEntity(
    val primaryKey: Long? = null,
    val url: String,
    val validityKey: String?,
    val expirationDateTime: Instant?,
    val rootPrimaryKey: Long?,
    val visibility: Visibility,
    val lifetime: Lifetime,
)

fun VersioningEntry.toEntity() = VersioningEntity(
    primaryKey = primaryKey,
    url = url,
    validityKey = validityKey,
    expirationDateTime = expirationDateTime?.let {
        Instant.parse(it)
    },
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)