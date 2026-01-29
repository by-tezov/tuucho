package com.tezov.tuucho.core.data.repository.database.entity

import com.tezov.tuucho.core.data.repository.database.table.HookEntry
import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility

internal data class HookEntity(
    val primaryKey: Long? = null,
    val url: String,
    val rootPrimaryKey: Long?,
    val visibility: JsonVisibility,
    val lifetime: JsonLifetime,
)

internal fun HookEntry.toEntity() = HookEntity(
    primaryKey = primaryKey,
    url = url,
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)
