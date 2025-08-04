package com.tezov.tuucho.core.domain.business.model.schema._system

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.tool.json.booleanOrNull
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class DelegateSchemaKey<T : Any?>(
    private val mapOperator: MapOperator,
    private val type: KClass<*>,
    private var key: String? = null,
) {

    interface MapOperator {
        fun read(key: String): JsonElement?
        fun write(key: String, jsonElement: JsonElement)
    }

    private fun resolveKey(property: KProperty<*>): String {
        return key ?: property.name.also { key = it }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val value = mapOperator.read(resolveKey(property)) ?: return null
        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        return (when (type) {
            JsonElement::class, JsonPrimitive::class, JsonArray::class, JsonObject::class -> value
            String::class -> value.stringOrNull
            Boolean::class -> value.booleanOrNull
            else -> throw DomainException.Default("unknown type")
        }) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value == null) {
            mapOperator.write(resolveKey(property), JsonNull)
        } else {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            mapOperator.write(
                resolveKey(property), when (type) {
                    JsonElement::class, JsonPrimitive::class, JsonArray::class, JsonObject::class -> value
                    String::class -> JsonPrimitive(value as String)
                    Boolean::class -> JsonPrimitive(value as Boolean)
                    else -> throw DomainException.Default("unknown type")
                } as JsonElement
            )
        }
    }
}