package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.Deferred

object DeferredExtension {
    fun <T> Deferred<T>.throwOnFailure() {
        invokeOnCompletion { throwable ->
            throwable?.let { throw it }
        }
    }
}
