package com.tezov.tuucho.core.data.parser.rectifier._elements.label

import com.tezov.tuucho.core.data.parser._schema.ContentSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import com.tezov.tuucho.core.data.parser._schema._element.label.LabelSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object TextLabelContentMatcher : Matcher {

    private fun isValue(
        path: JsonElementPath
    ) = path.lastSegment() == LabelSchema.Name.value

    private fun isSubsetLabel(
        parent: JsonObject
    ) =
        parent[HeaderSubsetSchema.Name.subset].stringOrNull == LabelSchema.Default.subset

    private fun isInsideTypeContent(
        parent: JsonObject
    ) = parent[HeaderTypeSchema.Name.type].stringOrNull == ContentSchema.Default.type

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!isValue(path)) return false
        val parent = element.find(path.parent()).jsonObject
        return isSubsetLabel(parent) && isInsideTypeContent(parent)
    }

}