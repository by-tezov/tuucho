package com.tezov.tuucho.core.presentation.ui.render

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.Type
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.NavigateAction
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreensFromRoutesUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress.Companion.rememberAnimationProgress
import com.tezov.tuucho.core.presentation.tool.modifier.thenIfNotNull
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.transition.TransitionFade.fade
import com.tezov.tuucho.core.presentation.ui.transition.TransitionNone.none
import com.tezov.tuucho.core.presentation.ui.transition.TransitionSlideHorizontal.slideHorizontal
import com.tezov.tuucho.core.presentation.ui.transition.TransitionSlideVertical.slideVertical
import com.tezov.tuucho.core.presentation.ui.transition._system.AbstractModifierTransition
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

interface TuuchoEngineProtocol {
    suspend fun start(
        url: String
    )

    @Suppress("ComposableNaming")
    @Composable
    fun display()
}

class TuuchoEngine(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val processAction: ProcessActionUseCase,
    private val registerToScreenTransitionEvent: RegisterToScreenTransitionEventUseCase,
    private val notifyNavigationTransitionCompleted: NotifyNavigationTransitionCompletedUseCase,
    private val getScreensFromRoutes: GetScreensFromRoutesUseCase,
) : TuuchoEngineProtocol {
    private data class Group(
        val screens: List<ScreenProtocol>,
        val transitionSpecObject: JsonObject,
    )

    private var foregroundGroup: Group? = null
    private var backgroundGroup: Group? = null
    private var transitionRequested = false
    private val redrawTrigger = mutableIntStateOf(0)

    override suspend fun start(
        url: String
    ) {
        useCaseExecutor.async(
            useCase = registerToScreenTransitionEvent,
            input = RegisterToScreenTransitionEventUseCase.Input(
                onEvent = { event ->
                    when (event) {
                        is Event.RequestTransition -> {
                            onRequestTransitionEvent(event)
                        }

                        is Event.Idle -> {
                            onIdleEvent(event)
                        }

                        is Event.PrepareTransition -> { /* nothing */ }

                        else -> {
                            throw UiException.Default("received unmanaged transition event $event")
                        }
                    }
                }
            )
        )
        useCaseExecutor.async(
            useCase = processAction,
            input = ProcessActionUseCase.Input.JsonElement(
                route = null,
                action = ActionModelDomain.from(
                    command = NavigateAction.Url.command,
                    authority = NavigateAction.Url.authority,
                    target = url,
                )
            ),
        )
    }

    private suspend fun onRequestTransitionEvent(
        event: Event.RequestTransition
    ) {
        @Suppress("UNCHECKED_CAST")
        foregroundGroup = Group(
            screens = useCaseExecutor
                .await(
                    useCase = getScreensFromRoutes,
                    input = GetScreensFromRoutesUseCase.Input(
                        routes = event.foregroundGroup.routes
                    )
                )?.screens as List<ScreenProtocol>,
            transitionSpecObject = event.foregroundGroup.transitionSpecObject
        )
        @Suppress("UNCHECKED_CAST")
        backgroundGroup = Group(
            screens = useCaseExecutor
                .await(
                    useCase = getScreensFromRoutes,
                    input = GetScreensFromRoutesUseCase.Input(
                        routes = event.backgroundGroup.routes
                    )
                )?.screens as List<ScreenProtocol>,
            transitionSpecObject = event.backgroundGroup.transitionSpecObject
        )
        transitionRequested = true
        redrawTrigger.intValue += 1
    }

    private suspend fun onIdleEvent(
        event: Event.Idle
    ) {
        @Suppress("UNCHECKED_CAST")
        foregroundGroup = Group(
            screens = useCaseExecutor
                .await(
                    useCase = getScreensFromRoutes,
                    input = GetScreensFromRoutesUseCase.Input(
                        routes = event.routes
                    )
                )?.screens as List<ScreenProtocol>,
            transitionSpecObject = JsonNull
                .withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
                .apply { type = Type.none }
                .collect()
        )
        backgroundGroup = null
        transitionRequested = false
        redrawTrigger.intValue += 1
    }

    @Composable
    override fun display() {
        val animationProgress = if (transitionRequested) {
            rememberAnimationProgress(redrawTrigger.intValue)
        } else {
            null
        }
        val screens = remember(redrawTrigger.intValue) {
            buildList<Pair<String, @Composable () -> Unit>> {
                backgroundGroup?.let { group ->
                    group.screens.forEach { screen ->
                        add(screen.route.id to {
                            screen.displayWithTransition(
                                animationProgress,
                                group.transitionSpecObject
                            )
                        })
                    }
                }
                foregroundGroup?.let { group ->
                    group.screens.forEach { screen ->
                        add(screen.route.id to {
                            screen.displayWithTransition(
                                animationProgress,
                                group.transitionSpecObject
                            )
                        })
                    }
                }
            }
        }
        screens.forEach { (id, screen) -> key(id) { screen.invoke() } }
        animationProgress?.let {
            LaunchedEffect(redrawTrigger.intValue) {
                coroutineScopes.event
                    .async(
                        throwOnFailure = true
                    ) {
                        animationProgress
                            .events
                            .once {
                                useCaseExecutor.async(
                                    useCase = notifyNavigationTransitionCompleted,
                                    input = Unit
                                )
                            }
                    }
                animationProgress.start()
            }
        }
    }

    @Suppress("ComposableNaming")
    @Composable
    private fun ScreenProtocol.displayWithTransition(
        animationProgress: AnimationProgress?,
        spec: JsonObject,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .thenIfNotNull(
                    animationProgress,
                    block = { modifierTransition(it, spec = spec) }
                )
        ) {
            display()
        }
    }

    @Suppress("ModifierFactoryExtensionFunction", "ModifierFactoryReturnType")
    @Composable
    private fun modifierTransition(
        animationProgress: AnimationProgress,
        spec: JsonObject,
    ): AbstractModifierTransition = spec
        .withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
        .let { scope ->
            when (scope.type) {
                Type.fade -> animationProgress.fade(
                    specObject = spec,
                )

                Type.slideHorizontal -> animationProgress.slideHorizontal(
                    specObject = spec
                )

                Type.slideVertical -> animationProgress.slideVertical(
                    specObject = spec
                )

                Type.none -> animationProgress.none()

                else -> throw UiException.Default("unknown transition type ${scope.type}")
            }
        }
}
