package com.tezov.tuucho.core.data.repository.database.entity

import com.tezov.tuucho.core.data.repository.database.table.HookEntry
import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility
import kotlinx.serialization.json.JsonArray

internal data class HookEntity(
    val primaryKey: Long? = null,
    val url: String,
    val urlWhiteList: JsonArray?,
    val rootPrimaryKey: Long?,
    val visibility: JsonVisibility,
    val lifetime: JsonLifetime,
)

internal fun HookEntry.toEntity() = HookEntity(
    primaryKey = primaryKey,
    url = url,
    urlWhiteList = urlWhiteList,
    rootPrimaryKey = rootPrimaryKey,
    visibility = visibility,
    lifetime = lifetime,
)
