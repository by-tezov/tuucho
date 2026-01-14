package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.domain.business.di.KoinMass
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import com.tezov.tuucho.shared.sample._system.Logger
import com.tezov.tuucho.uiComponent.stable.di.SystemUiModules
import kotlin.collections.List
import kotlin.collections.listOf
import kotlin.collections.plus
import kotlin.plus

internal expect fun SystemSharedModules.platformInvoke(): List<KoinMass>

object SystemSharedModules {

    fun invoke(): List<KoinMass> = listOf(
        module(ModuleContextCore.Main) {
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
