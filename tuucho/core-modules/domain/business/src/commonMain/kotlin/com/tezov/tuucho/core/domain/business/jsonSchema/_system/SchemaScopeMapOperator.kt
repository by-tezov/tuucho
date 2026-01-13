@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal class SchemaScopeMapOperator(
    root: String,
    argument: SchemaScopeArgument
) : OpenSchemaScope.MapOperator {
    private var _map: Map<String, JsonElement>? = null

    override val element: JsonElement by lazy {
        if (argument.moveOnRoot) {
            (argument.element as? JsonObject)?.get(root) ?: JsonNull
        } else {
            argument.element
        }
    }

    override val hasBeenChanged get() = _map is MutableMap

    override fun contains(
        key: String
    ) = resolveMap.contains(key)

    private val resolveMutableMap: MutableMap<String, JsonElement>
        get() {
            return (_map as? MutableMap<String, JsonElement>) ?: when (val element = element) {
                is JsonObject -> element.toMutableMap()
                is JsonPrimitive, is JsonArray -> mutableMapOf()
            }.also { _map = it }
        }

    private val resolveMap: Map<String, JsonElement>
        get() {
            return _map ?: when (val element = element) {
                is JsonObject -> element
                is JsonPrimitive, is JsonArray -> emptyMap()
            }.also { _map = it }
        }

    override fun read(
        key: String
    ) = resolveMap[key]

    override fun write(
        key: String,
        jsonElement: JsonElement
    ) {
        resolveMutableMap[key] = jsonElement
    }

    override fun remove(
        key: String
    ) {
        resolveMutableMap.remove(key)
    }

    override fun collect() = _map?.let(::JsonObject)
        ?: (element as? JsonObject)
        ?: emptyMap<String, JsonElement>().let(::JsonObject)
}
