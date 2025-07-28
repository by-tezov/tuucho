package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsRectifier
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.MaterialSchema
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
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

    @Suppress("RedundantSuspendModifier")
    suspend fun process(material: JsonObject): JsonObject = material.withScope(MaterialSchema::Scope).apply {

//        logAll("*******************************")
//        logAll(element)

        rootComponent?.let { rootComponent = componentRectifier.process("".toPath(), it).jsonObject }
        components?.let { components = componentRectifier.process("".toPath(), it).jsonArray }
        contents?.let { contents = contentRectifier.process("".toPath(), it).jsonArray }
        styles?.let { styles = styleRectifier.process("".toPath(), it).jsonArray }
        options?.let { options = optionRectifier.process("".toPath(), it).jsonArray }
        texts?.takeIf { it != JsonNull }?.let { texts = textsRectifier.process("".toPath(), it).jsonArray }
        colors?.takeIf { it != JsonNull }?.let { colors = colorsRectifier.process("".toPath(), it).jsonArray }
        dimensions?.takeIf { it != JsonNull }?.let { dimensions = dimensionsRectifier.process("".toPath(), it).jsonArray }

//        logAll(collect())
//        logAll("\n")

    }.collect()

}