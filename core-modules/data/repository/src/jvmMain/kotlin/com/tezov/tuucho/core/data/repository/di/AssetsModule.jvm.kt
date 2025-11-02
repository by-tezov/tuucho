package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsJvm
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

internal object AssetsModuleJvm {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            factory<AssetsProtocol> {
                AssetsJvm()
            }
        }
    }
}
