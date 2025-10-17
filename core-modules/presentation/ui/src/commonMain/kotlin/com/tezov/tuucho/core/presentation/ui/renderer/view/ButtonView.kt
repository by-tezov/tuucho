package com.tezov.tuucho.core.presentation.ui.renderer.view

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ButtonViewFactory(
    private val useCaseExecutor: UseCaseExecutor,
    private val actionHandler: ProcessActionUseCase,
) : AbstractViewFactory() {

    private val labelUiComponentFactory: LabelViewFactory by inject()

    override fun accept(
        componentElement: JsonObject,
    ) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == ButtonSchema.Component.Value.subset
    }

    override suspend fun process(
        route: NavigationRoute.Url,
        componentObject: JsonObject,
    ) = ButtonView(
        route = route,
        componentObject = componentObject,
        useCaseExecutor = useCaseExecutor,
        labelUiComponentFactory = labelUiComponentFactory,
        actionHandler = actionHandler
    ).also { it.init() }
}

class ButtonView(
    private val route: NavigationRoute.Url,
    componentObject: JsonObject,
    private val useCaseExecutor: UseCaseExecutor,
    private val labelUiComponentFactory: LabelViewFactory,
    private val actionHandler: ProcessActionUseCase,
) : AbstractView(componentObject) {

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
                        .process(route, labelObject)
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
            _action?.withScope(ActionSchema::Scope)
                ?.primary?.forEach {
                    useCaseExecutor.invoke(
                        useCase = actionHandler,
                        input = ProcessActionUseCase.Input(
                            route = route,
                            action = ActionModelDomain.from(it.string),
                            jsonElement = _action
                        )
                    )
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