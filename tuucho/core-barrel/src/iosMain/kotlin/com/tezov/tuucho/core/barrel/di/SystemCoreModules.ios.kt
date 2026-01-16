package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind


internal actual fun SystemCoreModules.platformInvoke(): List<KoinMass> = listOf(
    module(ModuleContextCore.Main) {
        factoryOf(::KoinIos) bind KoinIos::class
    }
)
