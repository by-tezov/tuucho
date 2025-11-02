package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

internal expect fun SystemSharedModules.platformInvoke(): List<ModuleProtocol>

object SystemSharedModules {

    fun invoke(): List<ModuleProtocol> = listOf(
        RequestInterceptorModule.invoke(),
        MiddlewareModule.invoke()
    ) + platformInvoke()

}
