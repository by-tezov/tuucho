package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

// from https://gist.github.com/elizarov/9a48b9709ffd508909d34fab6786acfe

@JvmInline
value class ReentrantMutex(
    val mutex: Mutex = Mutex()
) : Mutex by mutex, CoroutineContext.Key<ReentrantMutex>, CoroutineContext.Element {

    override val key get() = this

    suspend inline fun <T> withReentrantLock(crossinline block: suspend () -> T): T {
        if (currentCoroutineContext()[this] != null) return block()
        return withContext(this) {
            withLock { block() }
        }
    }
}

