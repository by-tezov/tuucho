package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.sample.shared._system.Logger
import com.tezov.tuucho.uiComponent.stable.di.SystemUiModules

internal expect fun SystemSharedModules.platformInvoke(): List<KoinMass>

object SystemSharedModules {

    fun invoke(): List<KoinMass> = listOf(
        module(ModuleContextCore.Main) {
            single {
                Logger(
                    systemInformation = get(),
                    exceptionVerbose = true
                )
            }
        },
        MonitorModule.invoke(),
        InteractionModule.invoke(),
        ExceptionHandlerModule.invoke(),
        MiddlewareModule.invoke(),
    ) + platformInvoke() + SystemUiModules.invoke()

}
