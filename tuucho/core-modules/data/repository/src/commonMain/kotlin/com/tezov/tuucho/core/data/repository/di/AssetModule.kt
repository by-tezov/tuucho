package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetSource
import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

object AssetModule {
    internal fun invoke() = module(ModuleContextData.Main) {
        factory<AssetSource>() bind AssetSourceProtocol::class
    }
}
