package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@TuuchoInternalApi
object SystemCoreDomainModules {
    fun invoke(): List<KoinMass> = listOf(
        MiscModule.invoke(),
        NavigationModule.invoke(),
        ActionModule.invoke(),
        UseCaseModule.invoke(),
        ValidatorModule.invoke()
    )
}
