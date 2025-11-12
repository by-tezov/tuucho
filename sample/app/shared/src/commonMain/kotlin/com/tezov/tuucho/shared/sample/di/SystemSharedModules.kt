package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.shared.sample._system.Logger

internal expect fun SystemSharedModules.platformInvoke(): List<ModuleProtocol>

object SystemSharedModules {

    fun invoke(): List<ModuleProtocol> = listOf(
        module(ModuleGroupCore.Main) { single { Logger() } },
        RequestInterceptorModule.invoke(),
        MiddlewareModule.invoke()
    ) + platformInvoke()

}
