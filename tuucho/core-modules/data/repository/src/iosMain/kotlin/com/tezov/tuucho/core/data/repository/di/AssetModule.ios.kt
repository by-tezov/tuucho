package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetReaderIos
import com.tezov.tuucho.core.data.repository.assets.AssetReaderProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object AssetModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<AssetReaderIos>() bind AssetReaderProtocol::class
    }
}
