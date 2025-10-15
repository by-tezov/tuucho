package com.tezov.tuucho.core.domain.business.jsonSchema._system

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T : OpenSchemaScope<T>> JsonElement.withScope(
    scopeFactory: (argument: SchemaScopeArgument) -> T,
    mapOperator: SchemaScopeMapOperator? = null,
): T {
    contract {
        callsInPlace(scopeFactory, InvocationKind.EXACTLY_ONCE)
    }
    return scopeFactory(
        SchemaScopeArgument(
            element = this, moveOnRoot = false, mapOperator = mapOperator
        )
    )
}

@OptIn(ExperimentalContracts::class)
fun <T : OpenSchemaScope<T>> JsonElement.onScope(
    scopeFactory: (argument: SchemaScopeArgument) -> T,
): T {
    contract {
        callsInPlace(scopeFactory, InvocationKind.EXACTLY_ONCE)
    }
    return scopeFactory(
        SchemaScopeArgument(
            element = this, moveOnRoot = true, mapOperator = null
        )
    )
}

data class SchemaScopeArgument(
    val element: JsonElement,
    val moveOnRoot: Boolean,
    val mapOperator: SchemaScopeMapOperator?,
)

interface SchemaScopeMapOperator : DelegateSchemaKey.MapOperator {
    val hasBeenChanged: Boolean
    fun contains(key: String): Boolean
    fun remove(key: String)
    fun collect(): JsonObject
}

class SchemaScope(
    argument: SchemaScopeArgument,
) : OpenSchemaScope<SchemaScope>(argument)

open class OpenSchemaScope<T : OpenSchemaScope<T>>(
    argument: SchemaScopeArgument,
) {
    open val root: String = ""

    val element: JsonElement by lazy {
        if (argument.moveOnRoot) {
            (argument.element as? JsonObject)?.get(root) ?: JsonNull
        } else {
            argument.element
        }
    }

    protected val mapOperator = argument.mapOperator ?: run {
        object : SchemaScopeMapOperator {

            private var _map: Map<String, JsonElement>? = null

            override val hasBeenChanged get() = _map is MutableMap

            override fun contains(key: String) = resolveMap.contains(key)

            private val resolveMutableMap: MutableMap<String, JsonElement>
                get() {
                    return (_map as? MutableMap<String, JsonElement>) ?: when (val element =
                        element) {
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

            override fun read(key: String) = resolveMap[key]

            override fun write(key: String, jsonElement: JsonElement) {
                resolveMutableMap[key] = jsonElement
            }

            override fun remove(key: String) {
                resolveMutableMap.remove(key)
            }

            override fun collect() = _map?.let(::JsonObject)
                ?: (element as? JsonObject)
                ?: emptyMap<String, JsonElement>().let(::JsonObject)
        }
    }

    val hasBeenChanged get() = mapOperator.hasBeenChanged

    fun contains(key: String) = mapOperator.contains(key)

    operator fun get(key: String): JsonElement? {
        return mapOperator.read(key)
    }

    operator fun set(key: String, value: JsonElement) {
        mapOperator.write(key, value)
    }

    fun keys(): Set<String> = (element as? JsonObject)?.keys ?: emptySet()

    fun remove(key: String) {
        mapOperator.remove(key)
    }

    protected inline fun <reified T : Any?> delegate(
        key: String? = null,
    ) = DelegateSchemaKey<T>(mapOperator, T::class, key)

    fun <T : OpenSchemaScope<T>> withScope(scopeFactory: (argument: SchemaScopeArgument) -> T) =
        element.withScope(scopeFactory, mapOperator)

    fun <T : OpenSchemaScope<T>> onScope(scopeFactory: (argument: SchemaScopeArgument) -> T) =
        element.onScope(scopeFactory)

    fun collect() = mapOperator.collect()

    fun collectChangedOrNull() = if (hasBeenChanged) mapOperator.collect() else null

    override fun toString(): String {
        return "initial=$element\ncurrent=${collect()}"
    }

}

