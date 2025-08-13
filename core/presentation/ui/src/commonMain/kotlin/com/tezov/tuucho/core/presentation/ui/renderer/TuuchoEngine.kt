package com.tezov.tuucho.core.presentation.ui.renderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tezov.tuucho.core.domain.business.usecase.GetVisibleScreensUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToNavigationUrlActionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateViewEventUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
import org.koin.compose.currentKoinScope

interface TuuchoEngineProtocol {

    suspend fun init(configUrl: String, initialUrl: String)

    @Composable
    fun display()
}

class TuuchoEngine(
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val registerUpdateViewEvent: RegisterUpdateViewEventUseCase,
    private val registerToNavigationUrlActionEvent: RegisterToNavigationUrlActionEventUseCase,
    private val registerToScreenTransitionEvent: RegisterToScreenTransitionEventUseCase,
    private val notifyNavigationTransitionCompleted: NotifyNavigationTransitionCompletedUseCase,
    private val getVisibleScreens: GetVisibleScreensUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : TuuchoEngineProtocol {

    private val screens = mutableStateOf<List<ScreenProtocol>>(emptyList())

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
                    @Suppress("UNCHECKED_CAST")
                    screens.value = getVisibleScreens.invoke(Unit).screens as List<ScreenProtocol>
                    if (it) {
                        notifyNavigationTransitionCompleted.invoke(Unit)
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

    @Composable
    override fun display() {
        screens.value.forEach { it.display() }
    }

}

@Composable
fun rememberTuuchoEngine(): TuuchoEngineProtocol = currentKoinScope().let { scope ->
    remember(scope) { scope.get(TuuchoEngineProtocol::class, null) }
}
