package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull

object ExtensionFlow {

    suspend fun <T : Any> MutableSharedFlow<T>.collectOnce(block: suspend (T) -> Unit) {
        firstOrNull {
            block(it)
            true
        }
    }

    suspend fun <T : Any> MutableSharedFlow<T>.collectForever(block: suspend (T) -> Unit) {
        collect { block(it) }
    }

    suspend fun <T : Any> MutableSharedFlow<T>.collectUntil(block: suspend (T) -> Boolean) {
        firstOrNull { block(it) }
    }

}