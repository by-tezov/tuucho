package com.tezov.tuucho.core.data.cache.parser.decoder

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.domain.model._system.SymbolDomain
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import com.tezov.tuucho.core.domain.model.material.TextModelDomain
import com.tezov.tuucho.core.domain.model.material.ValueOrObjectModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class TextModelDomainDecoder(
    private val database: Database,
) : JsonEntityDecoderToModelDomain<JsonElement> {

    override suspend fun decode(
        element: JsonElement,
        config: DecoderConfig
    ) = element
        .resolveRefs(config.url, TextModelDomain.Default.type, database)
        .merge()

    //TODO

    private suspend fun JsonElement.resolveRefs(
        url: String,
        type: String,
        database: Database
    ): List<JsonElement> {
        val entries = mutableListOf(this)
        var currentEntry = this
//        do {
//            val idRef = resolveIdRef(
////                currentEntry.jsonObject[HeaderIdsModelDomain.Name.id].stringOrNull,
////                currentEntry.jsonObject[HeaderIdsModelDomain.Name.idFrom].stringOrNull,
//                null, null
//            )
//            val entity = idRef?.let { ref ->
//
//                val id = "common"
//                val key = ref
//
//                database.jsonKeyValue().find(type = type, url = url, id = id, key = key)
//                    ?: database.jsonKeyValue().findShared(type = type, id = id, key = key)
//            }
//            if (entity != null) {
//                currentEntry = entity.jsonElement
//                entries.add(currentEntry)
//            }
//        } while (idRef != null && entity != null)
        return entries
    }

    private fun MutableMap<String, JsonElement>.merge(other: Map<String, JsonElement>) {
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

                key == ValueOrObjectModelDomain.Name.default -> {
                    if (value !is JsonNull) {
                        this[key] = value
                    }
                }

                else -> {
                    this[key] = value
                }
            }
        }
    }

    private fun List<JsonElement>.merge() = when (this.size) {
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

}