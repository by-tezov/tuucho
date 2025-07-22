package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.table.VersioningEntry

data class VersioningEntity(
    val primaryKey: Long? = null,
    val url: String,
    val version: String,
    val rootPrimaryKey: Long?,
    val isShared: Boolean,
)

fun VersioningEntry.toEntity() = VersioningEntity(
    primaryKey = primaryKey,
    url = url,
    version = version,
    rootPrimaryKey = rootPrimaryKey,
    isShared = isShared
)