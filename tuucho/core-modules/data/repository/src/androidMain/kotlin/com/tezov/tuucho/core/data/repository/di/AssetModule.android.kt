package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetReaderAndroid
import com.tezov.tuucho.core.data.repository.assets.AssetReaderProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

internal object AssetModuleAndroid {
    fun invoke() = module(ModuleContextData.Main) {
        @OptIn(TuuchoInternalApi::class)
        factory<AssetReaderProtocol> {
            AssetReaderAndroid(
                context = get(
                    SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT
                ),
            )
        }
    }
}
