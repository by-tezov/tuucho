package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsJvm
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module

internal object AssetsModuleJvm {
    fun invoke() = module(ModuleGroupData.Main) {
        factory<AssetsProtocol> {
            AssetsJvm()
        }
    }
}
