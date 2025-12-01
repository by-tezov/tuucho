package com.tezov.tuucho.core.presentation.ui.renderer.view

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ButtonViewFactory(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val interactionLockResolver: InteractionLockProtocol.Resolver,
    private val processAction: ProcessActionUseCase,
) : AbstractViewFactory() {
    private val labelViewFactory: LabelViewFactory by inject()

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
        interactionLockResolver = interactionLockResolver,
        labelViewFactory = labelViewFactory,
        coroutineScopes = coroutineScopes,
        actionHandler = processAction
    ).also { it.init() }
}

class ButtonView(
    private val route: NavigationRoute.Url,
    componentObject: JsonObject,
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val interactionLockResolver: InteractionLockProtocol.Resolver,
    private val labelViewFactory: LabelViewFactory,
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
                    labelView = labelViewFactory
                        .process(route, labelObject)
                }

                // TODO
//                componentObject = componentObject.withScope(ComponentSchema::Scope).apply {
//                    content = content?.withScope(ButtonSchema.Content::Scope).apply {
//                        remove(ButtonSchema.Content.Key.label)
//                    }?.collect()
//                }.collect()
            }
        }
    }

    private val action
        get(): () -> Unit = ({
            _action?.let {
                coroutineScopes.action
                    .async(
                        throwOnFailure = true
                    ) {
                        val screenLock = interactionLockResolver.tryAcquire(
                            requester = "$route::ButtonView::${hashCode().toHexString()}",
                            lockable = InteractionLockable.Type(
                                value = listOf(InteractionLockType.Screen)
                            )
                        )
                        if (screenLock is InteractionLockable.Empty) {
                            return@async
                        }
                        useCaseExecutor.await(
                            useCase = actionHandler,
                            input = ProcessActionUseCase.Input.ActionObject(
                                route = route,
                                actionObject = it,
                                lockable = screenLock.freeze()
                            )
                        )
                        interactionLockResolver.release(
                            requester = "$route::ButtonView::${hashCode().toHexString()}",
                            lockable = screenLock
                        )
                    }
            }
        })

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        Button(
            onClick = action,
            content = { labelView?.display(this) }
        )
    }
}
