package com.tezov.tuucho.core.presentation.ui.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationDirection
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationScreen
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting.AnimationSetting
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event.Idle
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event.RequestTransitionBackward
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event.RequestTransitionForward
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
import com.tezov.tuucho.core.presentation.ui.animation.AnimationFade.fade
import com.tezov.tuucho.core.presentation.ui.animation.AnimationNone
import com.tezov.tuucho.core.presentation.ui.animation.AnimationSlideHorizontal.slideHorizontal
import com.tezov.tuucho.core.presentation.ui.animation.AnimationSlideVertical.slideVertical
import com.tezov.tuucho.core.presentation.ui.animation._system.ModifierAnimation
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
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
        val animationScreen: AnimationScreen,
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
            RequestTransitionBackward -> {
                isNavigatingBack = true
                items.value = screensWithAnimationOptions.map {
                    Item(
                        screen = it.screen as ScreenProtocol,
                        animationScreen = it.animationScreen,
                        isVisible = true,
                        modifierAnimation = AnimationNone(),
                    )
                }
            }

            RequestTransitionForward -> {
                isNavigatingBack = false
                items.value = screensWithAnimationOptions.map {
                    Item(
                        screen = it.screen as ScreenProtocol,
                        animationScreen = it.animationScreen,
                        isVisible = true,
                        modifierAnimation = AnimationNone(),
                    )
                }
            }

            Idle -> {
                isNavigatingBack = null
                items.value = screensWithAnimationOptions.map {
                    Item(
                        screen = it.screen as ScreenProtocol,
                        animationScreen = it.animationScreen,
                        isVisible = true,
                        modifierAnimation = AnimationNone(),
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
        animationScreen: AnimationScreen,
        directionContent: AnimationDirection.Content,
    ) {
        val type = when (directionContent) {
            AnimationDirection.Content.Enter -> when (isNavigatingBack) {
                true -> animationScreen.enter.pop
                false -> animationScreen.enter.push
                else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
            }

            AnimationDirection.Content.Exit -> when (isNavigatingBack) {
                true -> animationScreen.exit.pop
                false -> animationScreen.exit.push
                else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
            }
        }
        when (type) {
            is AnimationSetting.None -> {
                modifierAnimation = AnimationNone()
            }

            is AnimationSetting.Fade -> {
                modifierAnimation = transition.fade(
                    config = type,
                    directionContent = directionContent,
                )
            }

            is AnimationSetting.SlideHorizontal -> {
                modifierAnimation = transition.slideHorizontal(
                    config = type,
                    directionNav = when (isNavigatingBack) {
                        true -> AnimationDirection.Navigation.Pop
                        false -> AnimationDirection.Navigation.Push
                        else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
                    },
                    directionContent = directionContent,
                )
            }

            is AnimationSetting.SlideVertical -> {
                modifierAnimation = transition.slideVertical(
                    config = type,
                    directionNav = when (isNavigatingBack) {
                        true -> AnimationDirection.Navigation.Pop
                        false -> AnimationDirection.Navigation.Push
                        else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
                    },
                    directionContent = directionContent,
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
            true -> priorEntries.last().animationScreen
            false -> lastEntries.last().animationScreen
            else -> throw UiException.Default("Shouldn't be possible, no navigation transition in progress")
        }

        println(animationOptionResolved)

        priorEntries.forEach {
            it.updateTransition(
                transition = transition,
                animationScreen = animationOptionResolved,
                directionContent = AnimationDirection.Content.Exit,
            )
        }
        lastEntries.forEach {
            it.updateTransition(
                transition = transition,
                animationScreen = animationOptionResolved,
                directionContent = AnimationDirection.Content.Enter,
            )
        }

        if (transition.isIdle) {
            coroutineScopes.event.async {
                transition
                    .events
                    .once {
                        println("once")

                        priorEntries.forEach { entry ->
                            entry.modifierAnimation = AnimationNone()
                        }
                        lastEntries.forEach { entry ->
                            entry.modifierAnimation = AnimationNone()
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
