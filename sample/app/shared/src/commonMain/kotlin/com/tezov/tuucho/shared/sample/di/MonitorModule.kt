package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import com.tezov.tuucho.core.domain.tool.async.CoroutineUncaughtExceptionHandler
import com.tezov.tuucho.shared.sample.monitor.LoggerCoroutineExceptionMonitor
import com.tezov.tuucho.shared.sample.monitor.LoggerInteractionLockMonitor
import com.tezov.tuucho.shared.sample.uncaughtException.LoggerCoroutineUncaughtExceptionHandler
import org.koin.core.module.Module

object MonitorModule {

    fun invoke() = module(ModuleGroupDomain.Main) {
        factory<CoroutineExceptionMonitor> {
            LoggerCoroutineExceptionMonitor(
                logger = get(),
                systemInformation = get()
            )
        }

        factory<InteractionLockMonitor> {
            LoggerInteractionLockMonitor(
                logger = get(),
                systemInformation = get()
            )
        }

        factory<CoroutineUncaughtExceptionHandler> {
            LoggerCoroutineUncaughtExceptionHandler(
                logger = get(),
                systemInformation = get()
            )
        }
    }
}
