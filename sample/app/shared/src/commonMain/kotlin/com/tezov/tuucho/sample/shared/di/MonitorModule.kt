package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.sample.shared.monitor.LoggerCoroutineExceptionMonitor
import com.tezov.tuucho.sample.shared.monitor.LoggerInteractionLockMonitor
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

object MonitorModule {

    fun invoke() = module(ModuleContextDomain.Main) {
        factory<LoggerCoroutineExceptionMonitor>() bind CoroutineExceptionMonitorProtocol::class
        factory<LoggerInteractionLockMonitor>() bind InteractionLockMonitorProtocol::class
    }
}
