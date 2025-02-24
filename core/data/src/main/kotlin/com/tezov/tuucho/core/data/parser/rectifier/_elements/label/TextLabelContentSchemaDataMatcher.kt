package com.tezov.tuucho.core.data.parser.rectifier._elements.label

import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser._schema.ContentSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import com.tezov.tuucho.core.data.parser._schema._element.label.LabelSchemaData
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object TextLabelContentSchemaDataMatcher : SchemaDataMatcher {

    private fun isValue(
        path: JsonElementPath
    ) = path.lastSegment() == LabelSchemaData.Name.value

    private fun isSubsetLabel(
        parent: JsonObject
    ) =
        parent[HeaderSubsetSchemaData.Name.subset].stringOrNull == LabelSchemaData.Default.subset

    private fun isInsideTypeContent(
        parent: JsonObject
    ) = parent[HeaderTypeSchemaData.Name.type].stringOrNull == ContentSchemaData.Default.type

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!isValue(path)) return false
        val parent = element.find(path.parent()).jsonObject
        return isSubsetLabel(parent) && isInsideTypeContent(parent)
    }

}