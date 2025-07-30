package com.tezov.tuucho.core.ui.uiComponentFactory

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ActionSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.ui.uiComponentFactory._system.Screen
import com.tezov.tuucho.core.ui.uiComponentFactory._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ButtonUiComponentFactory(
    private val actionHandler: ActionHandlerUseCase
) : UiComponentFactory() {

    private val labelUiComponentFactory: LabelUiComponentFactory by inject()

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == ButtonSchema.Component.Value.subset
    }

    override fun process(url: String, componentElement: JsonObject) = ButtonScreen(
        url = url,
        componentElement = componentElement,
        labelUiComponentFactory = labelUiComponentFactory,
        actionHandler = actionHandler
    ).also { it.init() }
}

class ButtonScreen(
    url: String,
    componentElement: JsonObject,
    private val labelUiComponentFactory: LabelUiComponentFactory,
    private val actionHandler: ActionHandlerUseCase
) : Screen(url, componentElement) {

    private var label: Screen? = null
    private var _action: JsonObject? = null

    override fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
        }
    }

    override fun JsonObject.processContent() {
        withScope(ButtonSchema.Content::Scope).run {
            action?.let { _action = it }
            label?.let {
                //TODO if label not null, push an update event on material state
                this@ButtonScreen.label = labelUiComponentFactory.process(url, it)
            }
        }

    }

    private val action
        get():() -> Unit = ({
            _action?.withScope(ActionSchema::Scope)?.run {
                value?.let { value ->
                    actionHandler.invoke(
                        url = url,
                        id = componentObject.idValue,
                        action = ActionModelDomain.Companion.from(value),
                        params = params
                    )
                }
            }
        })

    @Composable
    override fun showComponent(scope: Any?) {
        Button(
            onClick = action,
            content = { label?.show(this) }
        )
    }

}