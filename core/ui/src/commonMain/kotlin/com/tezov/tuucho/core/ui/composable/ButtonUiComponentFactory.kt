package com.tezov.tuucho.core.ui.composable

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope

import com.tezov.tuucho.core.domain.model.schema.material.ActionSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.ui.composable._system.ComposableScreenProtocol
import com.tezov.tuucho.core.ui.composable._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ButtonUiFactory : UiComponentFactory() {

    private val labelRendered: LabelRendered by inject()
    private val actionHandler: ActionHandlerUseCase by inject()

    override fun accept(componentElement: JsonObject) = componentElement
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                    it.withScope(SubsetSchema::Scope).self == ButtonSchema.Component.Value.subset
        }

    override fun process(componentElement: JsonObject): ComposableScreenProtocol {
        val id = componentElement.onScope(IdSchema::Scope).value
        val content = componentElement.onScope(ButtonSchema.Content::Scope)

        val label =  content.label?.let { labelRendered.process(it) }

        val actionScope = content.action?.withScope(ActionSchema::Scope)
        val actionValue = actionScope?.value
        val actionParams = actionScope?.params

        return ButtonScreen(
            label = label,
            action = {
                actionValue?.let { value ->
                    actionHandler.invoke(id, ActionModelDomain.Companion.from(value), actionParams)
                }
            }
        )
    }
}

class ButtonScreen(
    var label: ComposableScreenProtocol?,
    var action: () -> Unit,
) : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
        Button(
            onClick = action,
            content = { label?.show(this) }
        )
    }
}