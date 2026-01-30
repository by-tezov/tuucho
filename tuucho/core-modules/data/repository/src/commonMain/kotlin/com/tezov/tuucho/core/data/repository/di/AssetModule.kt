package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetSource
import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

object AssetModule {
    internal fun invoke() = module(ModuleContextData.Main) {
        factoryOf(::AssetSource) bind AssetSourceProtocol::class
    }
}
