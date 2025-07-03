package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.findOrNull
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

// segment
fun JsonElementPath.lastSegmentIs(
    value: String,
) = lastSegment() == value

fun JsonElementPath.lastSegmentIsAny(
    values: List<String>,
) = lastSegment().let { lastSegment -> values.any { it == lastSegment } }

fun JsonElementPath.lastSegmentStartWith(
    value: String,
) = lastSegment()?.startsWith(value) == true

// type
fun JsonElement?.isTypeOf(
    value: String,
) = (this as? JsonObject)?.schema()?.withScope(TypeSchema::Scope)?.self == value

fun JsonElementPath.isTypeOf(
    element: JsonElement, value: String,
) = element.findOrNull(this).isTypeOf(value)

fun JsonElementPath.parentIsTypeOf(
    element: JsonElement, value: String,
) = element.findOrNull(parent())?.isTypeOf(value) == true

// any type
fun JsonElement?.isAnyTypeOf(
    values: List<String>,
): Boolean {
    val type = (this as? JsonObject)?.schema()?.withScope(TypeSchema::Scope)?.self ?: return false
    return values.any { it == type }
}

fun JsonElementPath.isAnyTypeOf(
    element: JsonElement, values: List<String>,
) = element.findOrNull(this).isAnyTypeOf(values)

fun JsonElementPath.parentIsAnyTypeOf(
    element: JsonElement, values: List<String>,
) = element.findOrNull(parent())?.isAnyTypeOf(values) == true

// subset
fun JsonElement?.isSubsetOf(
    value: String,
) = (this as? JsonObject)?.schema()?.withScope(SubsetSchema::Scope)?.self == value

fun JsonElementPath.isSubsetOf(
    element: JsonElement, value: String,
) = element.findOrNull(this).isSubsetOf(value)

fun JsonElementPath.parentIsSubsetOf(
    element: JsonElement, value: String,
) = element.findOrNull(parent())?.isSubsetOf(value) == true

// any subset
fun JsonElement?.isAnySubsetOf(
    values: List<String>,
):Boolean {
    val type = (this as? JsonObject)?.schema()?.withScope(SubsetSchema::Scope)?.self ?: return false
    return values.any { it == type }
}

fun JsonElementPath.isAnySubsetOf(
    element: JsonElement, values: List<String>,
) = element.findOrNull(this).isAnySubsetOf(values)

fun JsonElementPath.parentIsAnySubsetOf(
    element: JsonElement, values: List<String>,
) = element.findOrNull(parent())?.isAnySubsetOf(values) == true

