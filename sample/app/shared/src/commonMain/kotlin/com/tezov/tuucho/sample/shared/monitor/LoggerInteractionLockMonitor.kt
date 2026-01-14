package com.tezov.tuucho.sample.shared.monitor

import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerInteractionLockMonitor(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : InteractionLockMonitor {

    override fun process(context: InteractionLockMonitor.Context) {
        with(context) {
            logger.debug("THREAD") { systemInformation.currentThreadName() }
            logger.debug("LOCK:$event") { "$requester - ${if (lockTypes.isEmpty()) "nothing" else lockTypes.toString()} " }
        }
    }
}
