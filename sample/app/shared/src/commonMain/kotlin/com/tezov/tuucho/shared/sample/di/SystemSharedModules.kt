package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.di.Koin
import com.tezov.tuucho.shared.sample._system.Logger
import com.tezov.tuucho.ui_component.stable.di.SystemUiModules

internal expect fun SystemSharedModules.platformInvoke(): List<Koin>

object SystemSharedModules {

    fun invoke(): List<Koin> = listOf(
        module(ModuleGroupCore.Main) {
            single {
                Logger(
                    exceptionVerbose = false
                )
            }
        },
        MonitorModule.invoke(),
        InteractionModule.invoke(),
        InterceptorModule.invoke(),
        MiddlewareModule.invoke(),
    ) + platformInvoke() +  SystemUiModules.invoke()

}
