package com.tezov.tuucho.core.presentation.ui.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionDirection
import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionScreen
import com.tezov.tuucho.core.domain.business.navigation.transition.spec.TransitionSpec
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event.Idle
import com.tezov.tuucho.core.domain.business.usecase.GetScreensWithAnimationOptionsUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToNavigationUrlActionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateViewEventUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress.Companion.updateAnimationProgress
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.transition.TransitionFade.fade
import com.tezov.tuucho.core.presentation.ui.transition.TransitionNone
import com.tezov.tuucho.core.presentation.ui.transition.TransitionSlideHorizontal.slideHorizontal
import com.tezov.tuucho.core.presentation.ui.transition.TransitionSlideVertical.slideVertical
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierAnimation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.currentKoinScope

interface TuuchoEngineProtocol {

    suspend fun init(configUrl: String, initialUrl: String)

    @Composable
    fun display()
}

class TuuchoEngine(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val registerUpdateViewEvent: RegisterUpdateViewEventUseCase,
    private val registerToNavigationUrlActionEvent: RegisterToNavigationUrlActionEventUseCase,
    private val registerToScreenTransitionEvent: RegisterToScreenTransitionEventUseCase,
    private val notifyNavigationTransitionCompleted: NotifyNavigationTransitionCompletedUseCase,
    private val getScreensWithAnimationOptions: GetScreensWithAnimationOptionsUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : TuuchoEngineProtocol {

    data class Item(
        val screen: ScreenProtocol,
        val transitionScreen: TransitionScreen,
        var isVisible: Boolean,
        var modifierAnimation: ModifierAnimation,
    )

    private val items = mutableStateOf<List<Item>>(emptyList())

    private var isNavigatingBack: Boolean? = null

    private var lastEntries: List<Item> = emptyList()

    private var priorEntries: List<Item> = emptyList()

    override suspend fun init(configUrl: String, initialUrl: String) {
        useCaseExecutor.invokeSuspend(
            useCase = refreshMaterialCache,
            input = RefreshMaterialCacheUseCase.Input(
                url = configUrl
            )
        )
        useCaseExecutor.invoke(
            useCase = registerUpdateViewEvent,
            input = Unit
        )
        useCaseExecutor.invoke(
            useCase = registerToNavigationUrlActionEvent,
            input = Unit
        )
        useCaseExecutor.invoke(
            useCase = registerToScreenTransitionEvent,
            input = RegisterToScreenTransitionEventUseCase.Input(
                onEvent = {
                    updateItems(
                        screensWithAnimationOptions = getScreensWithAnimationOptions.invoke(Unit),
                        event = it
                    )

                    GlobalScope.launch {
                        delay(250)
                        if(it != Idle) {
                            notifyNavigationTransitionCompleted.invoke(Unit)
                        }
                    }

                }
            )
        )
        useCaseExecutor.invoke(
            useCase = navigateToUrl,
            input = NavigateToUrlUseCase.Input(
                url = initialUrl
            )
        )
    }

    private fun updateItems(
        screensWithAnimationOptions: List<GetScreensWithAnimationOptionsUseCase.Output>,
        event: NavigationRepositoryProtocol.StackTransition.Event,
    ) {
        when (event) {
//            RequestTransitionBackward -> {
//                isNavigatingBack = true
//                items.value = screensWithAnimationOptions.map {
//                    Item(
//                        screen = it.screen as ScreenProtocol,
//                        transitionScreen = it.transitionScreen,
//                        isVisible = true,
//                        modifierAnimation = TransitionNone(),
//                    )
//                }
//            }
//
//            RequestTransitionForward -> {
//                isNavigatingBack = false
//                items.value = screensWithAnimationOptions.map {
//                    Item(
//                        screen = it.screen as ScreenProtocol,
//                        transitionScreen = it.transitionScreen,
//                        isVisible = true,
//                        modifierAnimation = TransitionNone(),
//                    )
//                }
//            }

            Idle -> {
                isNavigatingBack = null
                items.value = screensWithAnimationOptions.map {
                    Item(
                        screen = it.screen as ScreenProtocol,
                        transitionScreen = it.transitionScreen,
                        isVisible = true,
                        modifierAnimation = TransitionNone(),
                    )
                }
            }

            else -> {}
        }

    }

    @Composable
    override fun display() {
//        updateTransition()
        items.value.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.then(item.modifierAnimation)
            ) {
                item.screen.display()
            }
        }
    }

    private fun Item.updateTransition(
        transition: AnimationProgress,
        transitionScreen: TransitionScreen,
        directionScreen: TransitionDirection.Screen,
    ) {
        val type = when (directionScreen) {
            TransitionDirection.Screen.Enter -> when (isNavigatingBack) {
                true -> transitionScreen.enter.pop
                false -> transitionScreen.enter.push
                else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
            }

            TransitionDirection.Screen.Exit -> when (isNavigatingBack) {
                true -> transitionScreen.exit.pop
                false -> transitionScreen.exit.push
                else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
            }
        }
        when (type) {
            is TransitionSpec.None -> {
                modifierAnimation = TransitionNone()
            }

            is TransitionSpec.Fade -> {
                modifierAnimation = transition.fade(
                    spec = type,
                    directionScreen = directionScreen,
                )
            }

            is TransitionSpec.SlideHorizontal -> {
                modifierAnimation = transition.slideHorizontal(
                    spec = type,
                    directionNav = when (isNavigatingBack) {
                        true -> TransitionDirection.Navigation.Pop
                        false -> TransitionDirection.Navigation.Push
                        else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
                    },
                    directionScreen = directionScreen,
                )
            }

            is TransitionSpec.SlideVertical -> {
                modifierAnimation = transition.slideVertical(
                    spec = type,
                    directionNav = when (isNavigatingBack) {
                        true -> TransitionDirection.Navigation.Pop
                        false -> TransitionDirection.Navigation.Push
                        else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
                    },
                    directionScreen = directionScreen,
                )
            }
        }
    }

    @Composable
    private fun updateTransition() {
        if (isNavigatingBack == null) {
            println("isNavigatingBack == null")
            return
        }
        val transition = updateAnimationProgress()
        val lastEntries = remember { lastEntries }
        val priorEntries = remember { priorEntries }
        val animationOptionResolved = when (isNavigatingBack) {
            true -> priorEntries.last().transitionScreen
            false -> lastEntries.last().transitionScreen
            else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
        }

        println(animationOptionResolved)

        priorEntries.forEach {
            it.updateTransition(
                transition = transition,
                transitionScreen = animationOptionResolved,
                directionScreen = TransitionDirection.Screen.Exit,
            )
        }
        lastEntries.forEach {
            it.updateTransition(
                transition = transition,
                transitionScreen = animationOptionResolved,
                directionScreen = TransitionDirection.Screen.Enter,
            )
        }

        if (transition.isIdle) {
            coroutineScopes.event.async {
                transition
                    .events
                    .once {
                        println("once")

                        priorEntries.forEach { entry ->
                            entry.modifierAnimation = TransitionNone()
                        }
                        lastEntries.forEach { entry ->
                            entry.modifierAnimation = TransitionNone()
                        }
                        useCaseExecutor.invoke(
                            useCase = notifyNavigationTransitionCompleted,
                            input = Unit
                        )
                    }
            }
            println("transition.start()")
            transition.start()
        }
    }
}

@Composable
fun rememberTuuchoEngine(): TuuchoEngineProtocol = currentKoinScope().let { scope ->
    remember(scope) { scope.get(TuuchoEngineProtocol::class, null) }
}
