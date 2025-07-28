package com.tezov.tuucho.kmm

import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.kmm._system.KMPViewModel

class MainViewModel(
    private val registerNavigationUrlEvent: RegisterNavigationUrlEventUseCase,
): KMPViewModel() {

    private val _url = mutableStateOf("page-home")
    val url = _url

    fun init() {
        registerNavigationUrlEvent.invoke { _url.value = it }
    }

}