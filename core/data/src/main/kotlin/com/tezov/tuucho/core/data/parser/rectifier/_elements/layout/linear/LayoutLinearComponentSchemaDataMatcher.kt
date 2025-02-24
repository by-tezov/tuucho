package com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear

import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser._schema.ContentSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import com.tezov.tuucho.core.data.parser._schema._element.layout.LayoutLinearSchemaData
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object LayoutLinearComponentSchemaDataMatcher : SchemaDataMatcher {

    private fun isItems(
        path: JsonElementPath
    ) = path.lastSegment() == LayoutLinearSchemaData.Name.items

    private fun isSubsetLayoutLinear(
        parent: JsonObject
    ) =
        parent[HeaderSubsetSchemaData.Name.subset].stringOrNull == LayoutLinearSchemaData.Default.subset

    private fun isInsideTypeContent(
        parent: JsonObject
    ) = parent[HeaderTypeSchemaData.Name.type].stringOrNull == ContentSchemaData.Default.type

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!isItems(path)) return false
        val parent = element.find(path.parent()).jsonObject
        return isSubsetLayoutLinear(parent) && isInsideTypeContent(parent)
    }

}