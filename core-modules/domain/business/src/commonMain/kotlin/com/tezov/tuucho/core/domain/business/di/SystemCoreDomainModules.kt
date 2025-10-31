package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

object SystemCoreDomainModules {

    fun invoke(): List<ModuleProtocol> = listOf(
        MiscModule.invoke(),
        NavigationModule.invoke(),
        ActionProcessorModule.invoke(),
        UseCaseModule.invoke()
    )

}