package com.tezov.tuucho.core.data.parser.rectifier._element.button

import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.schema._element.label.LabelSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetPut
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class ContentButtonLabelRectifier : Rectifier() {

    private val matcher = ContentButtonLabelMatcher()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = with(element.find(path).jsonObject) {
        if (subsetOrNull == null) {
            toMutableMap().apply {
                subsetPut(LabelSchema.Component.Value.subset)
            }.let(::JsonObject)
        } else null
    }
}