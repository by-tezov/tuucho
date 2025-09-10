package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.table.HookEntry
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility

data class HookEntity(
    val primaryKey: Long? = null,
    val url: String,
    val rootPrimaryKey: Long?,
    val visibility: Visibility,
    val lifetime: Lifetime,
)

fun HookEntry.toEntity() = HookEntity(
    primaryKey = primaryKey,
    url = url,
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)