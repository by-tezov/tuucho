package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.data.parser._schema.header.HeaderTypeSchema.Companion.typeOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface Matcher {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean

    companion object {

        fun JsonElementPath.lastSegmentIs(
            value: String
        ) = lastSegment() == value

        fun JsonElementPath.isTypeOf(
            element: JsonElement, value: String
        ) = (element.find(this) as? JsonObject)
            ?.typeOrNull?.equals(value) == true

        fun JsonElementPath.parentIsTypeOf(
            element: JsonElement, value: String
        ) = element.findOrNull(parent())?.isTypeOf(value) == true

        fun JsonElement?.isTypeOf(
            value: String
        ) = (this as? JsonObject)?.typeOrNull == value

        fun JsonElementPath.parentIsSubsetOf(
            element: JsonElement, value: String
        ) = element.findOrNull(parent())?.isSubsetOf(value) == true

        fun JsonElement?.isSubsetOf(
            value: String
        ) = (this as? JsonObject)?.subsetOrNull == value

    }
}