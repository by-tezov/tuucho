package com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear

import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._schema.DefaultComponentSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchemaData
import com.tezov.tuucho.core.data.parser._schema._element.layout.LayoutLinearSchemaData
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object LayoutLinearContentSchemaDataRectifier : SchemaDataRectifier() {

    private fun isContent(
        path: JsonElementPath
    ) = path.lastSegment() == DefaultComponentSchemaData.Name.content

    private fun hasItems(
        path: JsonElementPath, element: JsonElement
    ) = element.find(path).jsonObject.containsKey(LayoutLinearSchemaData.Name.items)

    private fun isSubsetLayoutLinear(
        path: JsonElementPath, element: JsonElement
    ) = element.find(path.parent())
        .jsonObject[HeaderSubsetSchemaData.Name.subset].stringOrNull == LayoutLinearSchemaData.Default.subset

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = isContent(path)
            && isSubsetLayoutLinear(path, element)
            && hasItems(path, element)
}