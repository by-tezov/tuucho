package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsAndroid
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

internal object AssetsModuleAndroid {
    fun invoke() = module(ModuleContextData.Main) {
        @OptIn(TuuchoInternalApi::class)
        factory<AssetsProtocol> {
            AssetsAndroid(
                context = get(
                    SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT
                ),
            )
        }
    }
}
