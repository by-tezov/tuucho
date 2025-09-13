package com.tezov.tuucho.core.presentation.ui.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.Type
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event
import com.tezov.tuucho.core.domain.business.usecase.GetScreensFromRoutesUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress.Companion.rememberAnimationProgress
import com.tezov.tuucho.core.presentation.tool.modifier.thenOnNotNull
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.transition.TransitionFade.fade
import com.tezov.tuucho.core.presentation.ui.transition.TransitionNone.none
import com.tezov.tuucho.core.presentation.ui.transition.TransitionSlideHorizontal.slideHorizontal
import com.tezov.tuucho.core.presentation.ui.transition.TransitionSlideVertical.slideVertical
import com.tezov.tuucho.core.presentation.ui.transition._system.AbstractModifierTransition
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import org.koin.compose.currentKoinScope

interface TuuchoEngineProtocol {

    suspend fun load(url: String)
    
    suspend fun start(url: String)

    @Composable
    fun display()
}

class TuuchoEngine(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val registerToScreenTransitionEvent: RegisterToScreenTransitionEventUseCase,
    private val notifyNavigationTransitionCompleted: NotifyNavigationTransitionCompletedUseCase,
    private val getScreensFromRoutes: GetScreensFromRoutesUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : TuuchoEngineProtocol {

    private data class Group(
        val screens: List<ScreenProtocol>,
        val transitionSpecObject: JsonObject,
    )

    private var foregroundGroup: Group? = null
    private var backgroundGroup: Group? = null
    private var transitionRequested = false
    private val redrawTrigger = mutableStateOf(0)

    override suspend fun load(url: String) {
        useCaseExecutor.invokeSuspend(
            useCase = refreshMaterialCache,
            input = RefreshMaterialCacheUseCase.Input(
                url = url
            )
        )
    }
    
    override suspend fun start(url: String) {
        useCaseExecutor.invoke(
            useCase = registerToScreenTransitionEvent,
            input = RegisterToScreenTransitionEventUseCase.Input(
                onEvent = { event ->
                    when (event) {
                        is Event.RequestTransition -> onRequestTransitionEvent(event)
                        is Event.Idle -> onIdleEvent(event)
                        is Event.PrepareTransition -> { /* nothing */
                        }

                        else -> throw UiException.Default("received unmanaged transition event $event")
                    }
                }
            )
        )
        useCaseExecutor.invoke(
            useCase = navigateToUrl,
            input = NavigateToUrlUseCase.Input(
                url = url
            )
        )
    }

    private suspend fun onRequestTransitionEvent(event: Event.RequestTransition) {
        @Suppress("UNCHECKED_CAST")
        foregroundGroup = Group(
            screens = useCaseExecutor.invokeSuspend(
                useCase = getScreensFromRoutes,
                input = GetScreensFromRoutesUseCase.Input(
                    routes = event.foregroundGroup.routes
                )
            ).screens as List<ScreenProtocol>,
            transitionSpecObject = event.foregroundGroup.transitionSpecObject
        )
        @Suppress("UNCHECKED_CAST")
        backgroundGroup = Group(
            screens = useCaseExecutor.invokeSuspend(
                useCase = getScreensFromRoutes,
                input = GetScreensFromRoutesUseCase.Input(
                    routes = event.backgroundGroup.routes
                )
            ).screens as List<ScreenProtocol>,
            transitionSpecObject = event.backgroundGroup.transitionSpecObject
        )
        transitionRequested = true
        redrawTrigger.value = redrawTrigger.value + 1
    }

    private suspend fun onIdleEvent(event: Event.Idle) {
        @Suppress("UNCHECKED_CAST")
        foregroundGroup = Group(
            screens = useCaseExecutor.invokeSuspend(
                useCase = getScreensFromRoutes,
                input = GetScreensFromRoutesUseCase.Input(
                    routes = event.routes
                )
            ).screens as List<ScreenProtocol>,
            transitionSpecObject = JsonNull
                .withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
                .apply { type = Type.none }
                .collect()
        )
        backgroundGroup = null
        transitionRequested = false
        redrawTrigger.value = redrawTrigger.value + 1
    }

    @Composable
    override fun display() {
        val animationProgress = if (transitionRequested) {
            rememberAnimationProgress(redrawTrigger.value)
        } else null
        val screens = remember(redrawTrigger.value) {
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
            LaunchedEffect(redrawTrigger.value) {
                coroutineScopes.event.async {
                    animationProgress
                        .events
                        .once {
                            useCaseExecutor.invoke(
                                useCase = notifyNavigationTransitionCompleted,
                                input = Unit
                            )
                        }
                }
                animationProgress.start()
            }
        }
    }

    @Composable
    private fun ScreenProtocol.displayWithTransition(
        animationProgress: AnimationProgress?,
        spec: JsonObject,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .thenOnNotNull(
                    animationProgress,
                    block = { ModifierTransition(it, spec = spec) }
                )
        ) {
            display()
        }
    }

    @Composable
    private fun ModifierTransition(
        animationProgress: AnimationProgress,
        spec: JsonObject,
    ): AbstractModifierTransition = spec.withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
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

@Composable
fun rememberTuuchoEngine(): TuuchoEngineProtocol = currentKoinScope().let { scope ->
    remember(scope) { scope.get(TuuchoEngineProtocol::class, null) }
}
