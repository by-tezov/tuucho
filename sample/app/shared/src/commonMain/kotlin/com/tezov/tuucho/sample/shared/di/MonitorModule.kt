package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.sample.shared.monitor.LoggerCoroutineExceptionMonitor
import com.tezov.tuucho.sample.shared.monitor.LoggerInteractionLockMonitor
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

object MonitorModule {

    fun invoke() = module(ModuleContextDomain.Main) {
        factoryOf(::LoggerCoroutineExceptionMonitor) bind CoroutineExceptionMonitorProtocol::class
        factoryOf(::LoggerInteractionLockMonitor) bind InteractionLockMonitor::class
    }
}
