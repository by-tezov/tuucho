package com.tezov.tuucho.kmm

import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToNavigationUrlActionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateViewEventUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.kmm._system.KMPViewModel

class AppScreenViewModel(
    private val useCaseExecutor: UseCaseExecutor,
    private val registerUpdateViewEvent: RegisterUpdateViewEventUseCase,
    private val registerToNavigationUrlActionEvent: RegisterToNavigationUrlActionEventUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : KMPViewModel() {

    private val _screenIdentifier = mutableStateOf<ScreenProtocol.IdentifierProtocol?>(null)
    val screenIdentifier get() = _screenIdentifier.value

    fun init() {
        useCaseExecutor.invoke(
            useCase = registerUpdateViewEvent,
            input = Unit
        )
        useCaseExecutor.invoke(
            useCase = registerToNavigationUrlActionEvent,
            input = Unit
        )
        useCaseExecutor.invoke(
            useCase = navigateToUrl,
            input = NavigateToUrlUseCase.Input(
                url = "page-home"
            )
        )
    }

}