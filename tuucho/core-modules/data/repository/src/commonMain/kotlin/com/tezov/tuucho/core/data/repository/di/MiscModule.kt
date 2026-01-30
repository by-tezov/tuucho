package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.source._system.JsonLifetimeResolver
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf

internal object MiscModule {
    fun invoke() = module(ModuleContextData.Main) {
        factoryOf(::JsonLifetimeResolver)
    }
}
