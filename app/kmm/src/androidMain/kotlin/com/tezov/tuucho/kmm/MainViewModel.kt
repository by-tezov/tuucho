package com.tezov.tuucho.kmm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase

class MainViewModel(
    private val registerNavigationUrlEvent: RegisterNavigationUrlEventUseCase,
) : ViewModel() {

    private val _url = mutableStateOf("page-home")
    val url = _url

    fun init() {
        registerNavigationUrlEvent.invoke { _url.value = it }
    }

}