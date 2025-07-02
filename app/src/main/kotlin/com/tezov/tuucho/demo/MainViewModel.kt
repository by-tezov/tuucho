package com.tezov.tuucho.demo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class MainViewModel(
    coroutineDispatchers: CoroutineDispatchersProtocol,
    private val registerNavigationUrlEvent: RegisterNavigationUrlEventUseCase,
) : ViewModel() {

    private val coroutineScope = CoroutineScope(coroutineDispatchers.main)
    private val _url = mutableStateOf("page-home")
    val url = _url

    fun init() {
        coroutineScope.launch {
            registerNavigationUrlEvent
                .invoke { _url.value = it }
                .launchIn(this)
        }
    }

}