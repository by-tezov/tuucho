package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsIos
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.core.module.Module

internal object AssetsModuleIos {
    fun invoke() = module(ModuleGroupData.Main) {
        factory<AssetsProtocol> {
            AssetsIos()
        }
    }
}
