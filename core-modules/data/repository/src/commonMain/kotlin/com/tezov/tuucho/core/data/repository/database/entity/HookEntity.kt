package com.tezov.tuucho.core.data.repository.database.entity

import com.tezov.tuucho.core.data.repository.database.table.HookEntry
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility

internal data class HookEntity(
    val primaryKey: Long? = null,
    val url: String,
    val rootPrimaryKey: Long?,
    val visibility: Visibility,
    val lifetime: Lifetime,
)

internal fun HookEntry.toEntity() = HookEntity(
    primaryKey = primaryKey,
    url = url,
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)