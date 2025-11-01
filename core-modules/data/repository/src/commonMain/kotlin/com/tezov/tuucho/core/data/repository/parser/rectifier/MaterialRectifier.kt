package com.tezov.tuucho.core.data.repository.parser.rectifier

import com.tezov.tuucho.core.data.repository.parser.rectifier.action.ActionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.color.ColorsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.component.ComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.component.ComponentsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.content.ContentsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.dimension.DimensionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.option.OptionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.state.StatesRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.style.StylesRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.text.TextsRectifier
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

@OpenForTest
class MaterialRectifier : TuuchoKoinComponent {
    private val componentRectifier: ComponentRectifier by inject()
    private val componentsRectifier: ComponentsRectifier by inject()
    private val contentsRectifier: ContentsRectifier by inject()
    private val stylesRectifier: StylesRectifier by inject()
    private val optionsRectifier: OptionsRectifier by inject()
    private val stateRectifier: StatesRectifier by inject()
    private val textsRectifier: TextsRectifier by inject()
    private val colorsRectifier: ColorsRectifier by inject()
    private val dimensionsRectifier: DimensionsRectifier by inject()
    private val actionsRectifier: ActionsRectifier by inject()

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject
    ) = materialObject
        .withScope(MaterialSchema::Scope)
        .apply {
            rootComponent?.let {
                rootComponent = componentRectifier.process("".toPath(), it).jsonObject
            }
            components?.let { components = componentsRectifier.process("".toPath(), it).jsonArray }
            contents?.let { contents = contentsRectifier.process("".toPath(), it).jsonArray }
            styles?.let { styles = stylesRectifier.process("".toPath(), it).jsonArray }
            options?.let { options = optionsRectifier.process("".toPath(), it).jsonArray }
            states
                ?.takeIf { it != JsonNull }
                ?.let { states = stateRectifier.process("".toPath(), it).jsonArray }
            texts
                ?.takeIf { it != JsonNull }
                ?.let { texts = textsRectifier.process("".toPath(), it).jsonArray }
            colors
                ?.takeIf { it != JsonNull }
                ?.let { colors = colorsRectifier.process("".toPath(), it).jsonArray }
            dimensions
                ?.takeIf { it != JsonNull }
                ?.let { dimensions = dimensionsRectifier.process("".toPath(), it).jsonArray }
            actions
                ?.takeIf { it != JsonNull }
                ?.let { actions = actionsRectifier.process("".toPath(), it).jsonArray }
        }.collect()
}
