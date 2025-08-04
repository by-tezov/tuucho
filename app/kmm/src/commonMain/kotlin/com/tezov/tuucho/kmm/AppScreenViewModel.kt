package com.tezov.tuucho.kmm

import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.domain.business.usecase.InitiateAndRegisterToNavigationEventUseCase
import com.tezov.tuucho.kmm._system.KMPViewModel

class AppScreenViewModel(
    private val registerNavigationUrlEvent: InitiateAndRegisterToNavigationEventUseCase,
): KMPViewModel() {

    private val _triggerCount = mutableStateOf(0)
    val triggerCount = _triggerCount.value

    fun init() {
        registerNavigationUrlEvent.invoke("page-home") { _triggerCount.value += 1 }
    }

}