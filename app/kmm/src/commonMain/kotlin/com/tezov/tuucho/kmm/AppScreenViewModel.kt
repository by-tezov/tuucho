package com.tezov.tuucho.kmm

import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToNavigationUrlActionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToViewStackRepositoryEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateViewEventUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.kmm._system.KMPViewModel

class AppScreenViewModel(
    private val useCaseExecutor: UseCaseExecutor,
    private val registerUpdateViewEvent: RegisterUpdateViewEventUseCase,
    private val registerToNavigationUrlActionEvent: RegisterToNavigationUrlActionEventUseCase,
    private val registerToViewStackRepositoryEvent: RegisterToViewStackRepositoryEventUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : KMPViewModel() {

    private val _url = mutableStateOf("")
    val url get() = _url.value

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
            useCase = registerToViewStackRepositoryEvent,
            input = RegisterToViewStackRepositoryEventUseCase.Input(
                onEvent = { _url.value = it }
            )
        )
        useCaseExecutor.invoke(
            useCase = navigateToUrl,
            input = NavigateToUrlUseCase.Input(
                url = "page-home"
            )
        )
    }

}