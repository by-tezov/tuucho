package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.shared.sample.monitor.LoggerCoroutineExceptionMonitor
import org.koin.core.module.Module

object MonitorModule {

    fun invoke() = module(ModuleGroupDomain.Main) {
        coroutineException()
    }

    private fun Module.coroutineException() {
        factory<CoroutineExceptionMonitorProtocol> {
            LoggerCoroutineExceptionMonitor(
                logger = get(),
            )
        }
    }
}
