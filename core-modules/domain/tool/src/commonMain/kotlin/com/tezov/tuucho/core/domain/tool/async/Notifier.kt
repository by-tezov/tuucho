package com.tezov.tuucho.core.domain.tool.async

import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectForever
import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectOnce
import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectUntil
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter

object Notifier {
    class Emitter<T : Any>(
        replay: Int = 0,
        extraBufferCapacity: Int = 5,
        onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST,
    ) {
        private val flow = MutableSharedFlow<T>(
            replay = replay,
            extraBufferCapacity = extraBufferCapacity,
            onBufferOverflow = onBufferOverflow
        )

        fun tryEmit(
            event: T
        ) = flow.tryEmit(event)

        suspend fun emit(
            event: T
        ) = flow.emit(event)

        val createCollector get() = Collector(flow)
    }

    @JvmInline
    value class Collector<T : Any>(
        private val flow: Flow<T>,
    ) {
        fun filter(
            predicate: suspend (T) -> Boolean
        ): Collector<T> = Collector(flow.filter(predicate))

        suspend fun once(
            block: suspend (T) -> Unit
        ) = flow.collectOnce(block)

        suspend fun forever(
            block: suspend (T) -> Unit
        ) = flow.collectForever(block)

        suspend fun until(
            block: suspend (T) -> Boolean
        ) = flow.collectUntil(block)
    }
}
