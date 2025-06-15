package com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear

import com.tezov.tuucho.core.data.parser._schema.ContentSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import com.tezov.tuucho.core.data.parser._schema._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object LayoutLinearComponentMatcher : Matcher {

    private fun isItems(
        path: JsonElementPath
    ) = path.lastSegment() == LayoutLinearSchema.Name.items

    private fun isSubsetLayoutLinear(
        parent: JsonObject
    ) =
        parent[HeaderSubsetSchema.Name.subset].stringOrNull == LayoutLinearSchema.Default.subset

    private fun isInsideTypeContent(
        parent: JsonObject
    ) = parent[HeaderTypeSchema.Name.type].stringOrNull == ContentSchema.Default.type

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!isItems(path)) return false
        val parent = element.find(path.parent()).jsonObject
        return isSubsetLayoutLinear(parent) && isInsideTypeContent(parent)
    }

}