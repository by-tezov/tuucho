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

    fun finish() {
        coroutineScopes.default.async {
            _events.emit(Unit)
        }
    }

    fun onFinish(block: () -> Unit) {
        coroutineScopes.default.async {
            events.once { block() }
        }
    }
}
