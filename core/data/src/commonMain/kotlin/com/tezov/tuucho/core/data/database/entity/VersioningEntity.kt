package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.table.VersioningEntry
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility

data class VersioningEntity(
    val primaryKey: Long? = null,
    val url: String,
    val version: String?,
    val rootPrimaryKey: Long?,
    val visibility: Visibility,
    val lifetime: Lifetime,
)

fun VersioningEntry.toEntity() = VersioningEntity(
    primaryKey = primaryKey,
    url = url,
    version = version,
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)