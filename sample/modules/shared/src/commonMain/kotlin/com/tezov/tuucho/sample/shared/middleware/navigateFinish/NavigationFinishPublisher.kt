package com.tezov.tuucho.sample.shared.middleware.navigateFinish

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.channels.BufferOverflow

class NavigationFinishPublisher(
    private val coroutineScopes: CoroutineScopesProtocol
) {
    private val _events = Notifier.Emitter<Unit>(
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val events get() = _events.createCollector

    @OptIn(TuuchoInternalApi::class)
    fun finish() {
        coroutineScopes.default.asyncOnCompletionThrowing {
            _events.emit(Unit)
        }
    }

    @OptIn(TuuchoInternalApi::class)
    fun onFinish(block: () -> Unit) {
        coroutineScopes.default.asyncOnCompletionThrowing {
            events.once { block() }
        }
    }
}
