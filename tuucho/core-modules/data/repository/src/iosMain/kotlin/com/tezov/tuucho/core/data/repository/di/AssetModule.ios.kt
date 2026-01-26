package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetReaderIos
import com.tezov.tuucho.core.data.repository.assets.AssetReaderProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module

internal object AssetModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<AssetReaderProtocol> {
            AssetReaderIos()
        }
    }
}
