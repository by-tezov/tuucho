package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopesProtocol {

    fun launchOnDatabase(block: suspend CoroutineScope.() -> Unit)
    suspend fun <T> onDatabase(block: suspend CoroutineScope.() -> T): T

    fun launchOnNetwork(block: suspend CoroutineScope.() -> Unit)
    suspend fun <T> onNetwork(block: suspend CoroutineScope.() -> T): T

    fun launchOnParser(block: suspend CoroutineScope.() -> Unit)
    suspend fun <T> onParser(block: suspend CoroutineScope.() -> T): T

    fun launchOnEvent(block: suspend CoroutineScope.() -> Unit)
    suspend fun <T> onEvent(block: suspend CoroutineScope.() -> T): T

    fun launchOnUiProcessor(block: suspend CoroutineScope.() -> Unit)
    suspend fun <T> onUiProcessor(block: suspend CoroutineScope.() -> T): T
}
