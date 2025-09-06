package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.table.VersioningEntry
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

data class VersioningEntity(
    val primaryKey: Long? = null,
    val url: String,
    val validityKey: String?,
    val validityDateTime: LocalDateTime?,
    val validityTimeZone: TimeZone?,
    val rootPrimaryKey: Long?,
    val visibility: Visibility,
    val lifetime: Lifetime,
)

fun VersioningEntry.toEntity() = VersioningEntity(
    primaryKey = primaryKey,
    url = url,
    validityKey = validityKey,
    validityDateTime = validityDateTime?.let {
        LocalDateTime.parse(it)
    },
    validityTimeZone = validityTimeZone?.let {
        TimeZone.of(it)
    },
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)