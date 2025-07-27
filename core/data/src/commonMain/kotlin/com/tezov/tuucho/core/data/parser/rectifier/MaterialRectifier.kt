package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsRectifier
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema.material.MaterialSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialRectifier : KoinComponent {

    private val componentRectifier: ComponentRectifier by inject()
    private val contentRectifier: ContentRectifier by inject()
    private val styleRectifier: StyleRectifier by inject()
    private val optionRectifier: OptionRectifier by inject()

    private val textsRectifier: TextsRectifier by inject()
    private val colorsRectifier: ColorsRectifier by inject()
    private val dimensionsRectifier: DimensionsRectifier by inject()

    fun process(element: JsonElement): JsonObject {

//        runCatching {
//            if(element.jsonObject["root"]!!.jsonObject["id"].string == "page-home") {
//                logAll("*******************************")
//                logAll(element)
//            }
//        }

        val mutableMaterial = element.jsonObject.toMutableMap()

        mutableMaterial[MaterialSchema.Key.root]?.let { component ->
            mutableMaterial[MaterialSchema.Key.root] = componentRectifier.process("".toPath(), component)
        }
        mutableMaterial[MaterialSchema.Key.components]?.let {
            mutableMaterial[MaterialSchema.Key.components] = componentRectifier.process("".toPath(), it)
        }
        mutableMaterial[MaterialSchema.Key.contents]?.let {
            mutableMaterial[MaterialSchema.Key.contents] = contentRectifier.process("".toPath(), it)
        }
        mutableMaterial[MaterialSchema.Key.styles]?.let {
            mutableMaterial[MaterialSchema.Key.styles] = styleRectifier.process("".toPath(), it)
        }
        mutableMaterial[MaterialSchema.Key.options]?.let {
            mutableMaterial[MaterialSchema.Key.options] = optionRectifier.process("".toPath(), it)
        }

        mutableMaterial[MaterialSchema.Key.texts]?.let {
            mutableMaterial[MaterialSchema.Key.texts] = textsRectifier.process("".toPath(), it)
        }
        mutableMaterial[MaterialSchema.Key.colors]?.let {
            mutableMaterial[MaterialSchema.Key.colors] = colorsRectifier.process("".toPath(), it)
        }
        mutableMaterial[MaterialSchema.Key.dimensions]?.let {
            mutableMaterial[MaterialSchema.Key.dimensions] = dimensionsRectifier.process("".toPath(), it)
        }

//        runCatching {
//            if(element.jsonObject["root"]!!.jsonObject["id"].string == "page-home") {
//                logAll(JsonObject(mutableMaterial))
//                logAll("\n")
//            }
//        }

        return JsonObject(mutableMaterial)
    }

}