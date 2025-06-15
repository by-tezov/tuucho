package com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear

import com.tezov.tuucho.core.data.parser._schema.ContentSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser.rectifier.RectifierBase
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object LayoutLinearContentRectifier : RectifierBase() {

    private fun isContent(
        path: JsonElementPath
    ) = path.lastSegment() == ContentSchema.Default.type

    private fun hasItems(
        path: JsonElementPath, element: JsonElement
    ) = element.find(path).jsonObject.containsKey(LayoutLinearSchema.Name.items)

    private fun isSubsetLayoutLinear(
        path: JsonElementPath, element: JsonElement
    ) = element.find(path.parent())
        .jsonObject[HeaderSubsetSchema.Name.subset].stringOrNull == LayoutLinearSchema.Default.subset

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = isContent(path)
            && isSubsetLayoutLinear(path, element)
            && hasItems(path, element)
}