package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.findOrNull
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

fun JsonElementPath.lastSegmentIs(
    value: String
) = lastSegment() == value

fun JsonElementPath.lastSegmentStartWith(
    value: String
) = lastSegment()?.startsWith(value) == true

fun JsonElementPath.isTypeOf(
    element: JsonElement, value: String
) = (element.find(this) as? JsonObject)
    ?.typeOrNull?.equals(value) == true

fun JsonElement?.isTypeOf(
    value: String
) = (this as? JsonObject)?.typeOrNull == value

fun JsonElementPath.parentIsTypeOf(
    element: JsonElement, value: String
) = element.findOrNull(parent())?.isTypeOf(value) == true

fun JsonElement?.isSubsetOf(
    value: String
) = (this as? JsonObject)?.subsetOrNull == value

fun JsonElementPath.parentIsSubsetOf(
    element: JsonElement, value: String
) = element.findOrNull(parent())?.isSubsetOf(value) == true
