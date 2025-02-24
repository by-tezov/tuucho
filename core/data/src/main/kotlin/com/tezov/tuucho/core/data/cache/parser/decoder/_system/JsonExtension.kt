package com.tezov.tuucho.core.data.cache.parser.decoder._system

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.domain.model._system.SymbolDomain
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

suspend fun JsonElement.resolveRefs(
    url: String,
    type: String,
    database: Database
): List<JsonElement> {
    val entries = mutableListOf(this)
    var currentEntry = this
    do {
        val idRef = resolveIdRef(
//            currentEntry.jsonObject[HeaderIdsModelDomain.Name.id].stringOrNull,
//            currentEntry.jsonObject[HeaderIdsModelDomain.Name.idFrom].stringOrNull,
            null, null
        )
        val entity = idRef?.let { ref ->
            database.jsonEntity()
                .find(type = type, url = url, id = ref)
                ?: database.jsonEntity()
                    .findShared(type = type, id = ref)
        }
        if (entity != null) {
            currentEntry = entity.jsonElement
            entries.add(currentEntry)
        }
    } while (idRef != null && entity != null)
    return entries
}


fun MutableMap<String, JsonElement>.merge(other: Map<String, JsonElement>) {
    for ((key, value) in other) {
        when {
            value is JsonObject && this[key] is JsonObject -> {
                this[key]!!.jsonObject.toMutableMap()
                    .apply { merge(value.toMap()) }
                    .also { this[key] = JsonObject(it) }
            }
            //Hack : should be solved when refactor encoder on JsonElement
            key == HeaderIdModelDomain.Name.id -> {
                value.stringOrNull?.takeIf { !it.startsWith(SymbolDomain.ID_REF_INDICATOR) }?.let {
                    this[key] = value
                } ?: run {
                    this[key] = JsonNull
                }
            }
            else -> this[key] = value
        }
    }
}

fun List<JsonElement>.merge() = when (this.size) {
    0 -> JsonNull

    1 -> this.first()

    else -> {
        val mergedMap = mutableMapOf<String, JsonElement>()
        for (entry in this.asReversed()) {
            //mergedMap.merge(entry.jsonObject - HeaderIdsModelDomain.Name.idFrom)
        }
        mergedMap.let(::JsonObject)
    }
}