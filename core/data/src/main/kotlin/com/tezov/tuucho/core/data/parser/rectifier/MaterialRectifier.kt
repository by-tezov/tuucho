package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsRectifier
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.schema.MaterialSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialRectifier : KoinComponent {

    private val colorsRectifier: ColorsRectifier by inject()
    private val dimensionsRectifier: DimensionsRectifier by inject()
    private val textsRectifier: TextsRectifier by inject()

    private val styleRectifier: StyleRectifier by inject()
    private val contentRectifier: ContentRectifier by inject()
    private val componentRectifier: ComponentRectifier by inject()

    fun process(material: JsonElement): JsonElement {
        val materialElementMap = material.jsonObject.toMutableMap()

        materialElementMap[MaterialSchema.Key.colors]?.let {
            materialElementMap[MaterialSchema.Key.colors] = colorsRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Key.dimensions]?.let {
            materialElementMap[MaterialSchema.Key.dimensions] = dimensionsRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Key.texts]?.let {
            materialElementMap[MaterialSchema.Key.texts] = textsRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchema.Key.styles]?.let {
            materialElementMap[MaterialSchema.Key.styles] = styleRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Key.contents]?.let {
            materialElementMap[MaterialSchema.Key.contents] = contentRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchema.Key.components]?.let {
            materialElementMap[MaterialSchema.Key.components] = componentRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchema.Key.root]?.let { component ->
            materialElementMap[MaterialSchema.Key.root] = componentRectifier.process("".toPath(), component)
        }

        return JsonObject(materialElementMap)
    }

}