package com.tezov.tuucho.core.domain.tool.async

import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectForever
import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectOnce
import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectUntil
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

object Notifier {

    class Emitter<T : Any>(
        replay: Int = 0,
        extraBufferCapacity: Int = 1,
        onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST,
    ) {
        internal val flow = MutableSharedFlow<T>(
            replay = replay,
            extraBufferCapacity = extraBufferCapacity,
            onBufferOverflow = onBufferOverflow
        )

        fun tryEmit(event: T) = flow.tryEmit(event)

        suspend fun emit(event: T) = flow.emit(event)

        val createCollector get() = Collector(this)
    }

    class Collector<T : Any>(
        private val emitter: Emitter<T>,
    ) {

        suspend fun filter(block: suspend (T) -> Unit) = emitter.flow.filter

        suspend fun once(block: suspend (T) -> Unit) = emitter.flow.collectOnce(block)

        suspend fun forever(block: suspend (T) -> Unit) = emitter.flow.collectForever(block)

        suspend fun until(block: suspend (T) -> Boolean) = emitter.flow.collectUntil(block)

    }

}