package com.tezov.tuucho.kmm._system

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class KMPViewModel {
    private val viewModelJob = SupervisorJob()
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    open fun onCleared() {
        viewModelJob.cancel()
    }
}