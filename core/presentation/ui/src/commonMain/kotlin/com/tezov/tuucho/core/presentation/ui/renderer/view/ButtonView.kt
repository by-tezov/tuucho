package com.tezov.tuucho.core.presentation.ui.renderer.view

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.content.action.ActionSchema
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenIdentifier
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewIdentifier
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewIdentifierFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ButtonViewFactory(
    private val identifierFactory: ViewIdentifierFactory,
    private val useCaseExecutor: UseCaseExecutor,
    private val actionHandler: ActionHandlerUseCase,
) : ViewFactory() {

    private val labelUiComponentFactory: LabelViewFactory by inject()

    override fun accept(
        componentElement: JsonObject,
    ) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == ButtonSchema.Component.Value.subset
    }

    override suspend fun process(
        screenIdentifier: ScreenIdentifier,
        componentObject: JsonObject,
    ) = ButtonView(
        identifier = identifierFactory.invoke(screenIdentifier),
        componentObject = componentObject,
        useCaseExecutor = useCaseExecutor,
        labelUiComponentFactory = labelUiComponentFactory,
        actionHandler = actionHandler
    ).also { it.init() }
}

class ButtonView(
    identifier: ViewIdentifier,
    componentObject: JsonObject,
    private val useCaseExecutor: UseCaseExecutor,
    private val labelUiComponentFactory: LabelViewFactory,
    private val actionHandler: ActionHandlerUseCase,
) : View(identifier, componentObject) {

    override val children: List<ViewProtocol>?
        get() = labelView?.let { listOf(it) }

    private var labelView: ViewProtocol? = null
    private var _action: JsonObject? = null

    override suspend fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
        }
    }

    override suspend fun JsonObject.processContent() {
        withScope(ButtonSchema.Content::Scope).run {
            action?.let { _action = it }
            label?.let { labelObject ->
                labelView?.update(labelObject) ?: run {
                    labelView = labelUiComponentFactory
                        .process(identifier.screenIdentifier, labelObject)
                }

                //TODO
//                componentObject = componentObject.withScope(ComponentSchema::Scope).apply {
//                    content = content?.withScope(ButtonSchema.Content::Scope).apply {
//                        remove(ButtonSchema.Content.Key.label)
//                    }?.collect()
//                }.collect()
            }
        }
    }

    private val action
        get():() -> Unit = ({
            _action?.withScope(ActionSchema::Scope)?.run {
                value?.let { value ->
                    useCaseExecutor.invoke(
                        useCase = actionHandler,
                        input = ActionHandlerUseCase.Input(
                            source = identifier,
                            action = ActionModelDomain.Companion.from(value),
                            jsonElement = element
                        )
                    )
                }
            }
        })

    @Composable
    override fun displayComponent(scope: Any?) {
        Button(
            onClick = action,
            content = { labelView?.display(this) }
        )
    }

}