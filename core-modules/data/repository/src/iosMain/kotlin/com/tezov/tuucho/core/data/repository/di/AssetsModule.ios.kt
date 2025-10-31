package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.data.repository.assets.AssetsIos
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import org.koin.dsl.module

internal object AssetsModuleIos {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            factory<AssetsProtocol> {
                AssetsIos()
            }
        }
    }

}