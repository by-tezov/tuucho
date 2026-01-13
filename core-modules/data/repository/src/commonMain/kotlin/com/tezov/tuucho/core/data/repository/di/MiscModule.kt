package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import org.koin.core.module.dsl.factoryOf

internal object MiscModule {
    fun invoke() = module(ModuleGroupData.Main) {
        factoryOf(::LifetimeResolver)
    }
}
