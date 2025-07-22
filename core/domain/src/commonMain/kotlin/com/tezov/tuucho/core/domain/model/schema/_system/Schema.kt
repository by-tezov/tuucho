package com.tezov.tuucho.core.domain.model.schema._system

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Schema private constructor(
    val element: JsonElement,
) {

    companion object {
        fun JsonElement.schema() = Schema(this)
    }

    @OptIn(ExperimentalContracts::class)
    fun <T : OpenSchemaScope<T>> withScope(
        scopeFactory: () -> T,
    ): T {
        contract {
            callsInPlace(scopeFactory, InvocationKind.EXACTLY_ONCE)
        }
        return scopeFactory().also { it.schema = this }
    }

    @OptIn(ExperimentalContracts::class)
    fun <T : OpenSchemaScope<T>> onScope(
        scopeFactory: () -> T,
    ): T {
        contract {
            callsInPlace(scopeFactory, InvocationKind.EXACTLY_ONCE)
        }
        return scopeFactory().also {
            it.schema = Schema((element as? JsonObject)?.get(it.root) ?: JsonNull)
        }
    }

}

class SchemaScope : OpenSchemaScope<SchemaScope>()

open class OpenSchemaScope<T : OpenSchemaScope<T>> {
    open val root: String = ""

    lateinit var schema: Schema
        internal set //TODO "private file"

    val element get() = schema.element

    protected val mapOperator = object : DelegateSchemaKey.MapOperator {

        private var _map: Map<String, JsonElement>? = null

        private val resolveMutableMap: MutableMap<String, JsonElement>
            get() {
                return (_map as? MutableMap<String, JsonElement>) ?: when (val element = element) {
                    is JsonObject -> element.toMutableMap()
                    is JsonPrimitive -> mutableMapOf()
                    else -> error("element ${element::class.simpleName} can't be resolved as Mutable Map")
                }.also { _map = it }
            }

        private val resolveMap: Map<String, JsonElement>
            get() {
                return _map ?: when (val element = element) {
                    is JsonObject -> element
                    is JsonPrimitive -> emptyMap()
                    else -> error("element ${element::class.simpleName} can't be resolved as Map")
                }.also { _map = it }
            }

        override fun read(key: String) = resolveMap[key]

        override fun write(key: String, jsonElement: JsonElement) {
            resolveMutableMap[key] = jsonElement
        }

        override fun collect() = _map?.let { JsonObject(it) } ?: element.jsonObject
    }

    operator fun get(key: String): JsonElement? {
        return mapOperator.read(key)
    }

    operator fun set(key: String, value: JsonElement) {
        mapOperator.write(key, value)
    }

    protected inline fun <reified T : Any?> delegate(
        key: String? = null,
    ) = DelegateSchemaKey<T>(mapOperator, T::class, key)

    fun <T : OpenSchemaScope<T>> withScope(scopeFactory: () -> T) = schema.withScope(scopeFactory)

    fun <T : OpenSchemaScope<T>> onScope(scopeFactory: () -> T) = schema.onScope(scopeFactory)

    fun collect() = mapOperator.collect()

}

