package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.di.Koin
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import com.tezov.tuucho.core.domain.tool.async.CoroutineUncaughtExceptionHandler
import com.tezov.tuucho.shared.sample.monitor.LoggerCoroutineExceptionMonitor
import com.tezov.tuucho.shared.sample.monitor.LoggerInteractionLockMonitor
import com.tezov.tuucho.shared.sample.uncaughtException.LoggerCoroutineUncaughtExceptionHandler
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

object MonitorModule {

    fun invoke() = module(ModuleGroupDomain.Main) {
        factoryOf(::LoggerCoroutineExceptionMonitor) bind CoroutineExceptionMonitor::class

        factoryOf(::LoggerInteractionLockMonitor) bind InteractionLockMonitor::class

        factoryOf(::LoggerCoroutineUncaughtExceptionHandler) bind CoroutineUncaughtExceptionHandler::class
    }
}
