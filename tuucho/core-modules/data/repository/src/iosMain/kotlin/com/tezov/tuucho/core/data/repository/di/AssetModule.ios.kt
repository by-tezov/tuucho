package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsIos
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module

internal object AssetsModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<AssetReaderProtocol> {
            AssetReaderIos()
        }
    }
}
