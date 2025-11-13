package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.shared.sample.monitor.CoroutineExceptionMonitor
import org.koin.core.module.Module

object MonitorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = module(ModuleGroupData.RequestInterceptor) {
        coroutineException()
    }

    private fun Module.coroutineException() {
        factory<CoroutineExceptionMonitorProtocol> {
            CoroutineExceptionMonitor(
                logger = get(),
            )
        }
    }
}
