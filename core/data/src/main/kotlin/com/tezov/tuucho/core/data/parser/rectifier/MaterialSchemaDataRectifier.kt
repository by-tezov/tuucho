package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser._schema.MaterialSchemaData
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsSchemaDataRectifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialSchemaDataRectifier : KoinComponent {

    private val colorsRectifier: ColorsSchemaDataRectifier by inject()
    private val dimensionsRectifier: DimensionsSchemaDataRectifier by inject()
    private val textsRectifier: TextsSchemaDataRectifier by inject()

    private val optionRectifier: OptionSchemaDataRectifier by inject()
    private val styleRectifier: StyleSchemaDataRectifier by inject()
    private val contentRectifier: ContentSchemaDataRectifier by inject()
    private val componentRectifier: ComponentSchemaDataRectifier by inject()

    fun rectify(materialElement: JsonObject): JsonObject {
        val materialElementMap = materialElement.toMutableMap()

        materialElementMap[MaterialSchemaData.Name.colors]?.let {
            materialElementMap[MaterialSchemaData.Name.colors] = colorsRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchemaData.Name.dimensions]?.let {
            materialElementMap[MaterialSchemaData.Name.dimensions] = dimensionsRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchemaData.Name.texts]?.let {
            materialElementMap[MaterialSchemaData.Name.texts] = textsRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchemaData.Name.options]?.let {
            materialElementMap[MaterialSchemaData.Name.options] = optionRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchemaData.Name.styles]?.let {
            materialElementMap[MaterialSchemaData.Name.styles] = styleRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchemaData.Name.contents]?.let {
            materialElementMap[MaterialSchemaData.Name.contents] = contentRectifier.process("".toPath(), it)
        }

        materialElementMap[MaterialSchemaData.Name.components]?.let {
            materialElementMap[MaterialSchemaData.Name.components] = componentRectifier.process("".toPath(), it)
        }
        materialElementMap[MaterialSchemaData.Name.root]?.let { component ->
            materialElementMap[MaterialSchemaData.Name.root] = componentRectifier.process("".toPath(), component)
        }
        return JsonObject(materialElementMap)
    }

}