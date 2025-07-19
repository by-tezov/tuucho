package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.ActionSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ButtonRendered : Renderer() {

    private val labelRendered: LabelRendered by inject()
    private val actionHandler: ActionHandlerUseCase by inject()

    override fun accept(element: JsonElement) = element.schema()
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                    it.withScope(SubsetSchema::Scope).self == ButtonSchema.Component.Value.subset
        }

    override fun process(element: JsonElement): ComposableScreenProtocol {
        val schema = element.schema()
        val id = schema.withScope(IdSchema::Scope).self.stringOrNull
        val content = schema.onScope(ButtonSchema.Content::Scope)

        val label =  content.label?.let { labelRendered.process(it) }

        val actionScope = content.action?.schema()?.withScope(ActionSchema::Scope)
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