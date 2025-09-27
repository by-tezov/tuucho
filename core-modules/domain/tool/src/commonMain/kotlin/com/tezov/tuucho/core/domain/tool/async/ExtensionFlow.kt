package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

object ExtensionFlow {

    suspend fun <T : Any> Flow<T>.collectOnce(block: suspend (T) -> Unit) {
        firstOrNull {
            block(it)
            true
        }
    }

    suspend fun <T : Any> Flow<T>.collectForever(block: suspend (T) -> Unit) {
        collect { block(it) }
    }

    suspend fun <T : Any> Flow<T>.collectUntil(block: suspend (T) -> Boolean) {
        firstOrNull { block(it) }
    }

}