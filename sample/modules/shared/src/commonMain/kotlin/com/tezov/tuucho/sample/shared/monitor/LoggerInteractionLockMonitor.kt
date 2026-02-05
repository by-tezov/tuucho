package com.tezov.tuucho.sample.shared.monitor

import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitorProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerInteractionLockMonitor(
    private val logger: Logger
) : InteractionLockMonitorProtocol {

    override suspend fun process(context: InteractionLockMonitorProtocol.Context) {
        with(context) {
            logger.thread()
            logger.debug("LOCK:$event") { "$requester - ${if (lockTypes.isEmpty()) "nothing" else lockTypes.toString()} " }
        }
    }
}
