package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser._schema.MaterialSchema
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsRectifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialRectifier : KoinComponent {

    private val colorsRectifier: ColorsRectifier by inject()
    private val dimensionsRectifier: DimensionsRectifier by inject()
    private val textsRectifier: TextsRectifier by inject()

    private val optionRectifier: OptionRectifier by inject()
    private val styleRectifier: StyleRectifier by inject()
    private val contentRectifier: ContentRectifier by inject()
    private val componentRectifier: ComponentRectifier by inject()

    fun rectify(materialElement: JsonObject): JsonObject {
        val materialElementMap = materialElement.toMutableMap()

        materialElementMap[MaterialSchema.Name.colors]?.let {
            materialElementMap[MaterialSchema.Name.colors] = colorsRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Name.dimensions]?.let {
            materialElementMap[MaterialSchema.Name.dimensions] = dimensionsRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Name.texts]?.let {
            materialElementMap[MaterialSchema.Name.texts] = textsRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchema.Name.options]?.let {
            materialElementMap[MaterialSchema.Name.options] = optionRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Name.styles]?.let {
            materialElementMap[MaterialSchema.Name.styles] = styleRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchema.Name.contents]?.let {
            materialElementMap[MaterialSchema.Name.contents] = contentRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchema.Name.components]?.let {
            materialElementMap[MaterialSchema.Name.components] = componentRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchema.Name.root]?.let { component ->
            materialElementMap[MaterialSchema.Name.root] = componentRectifier.process("".toPath(), component)
        }

        return JsonObject(materialElementMap)
    }

}