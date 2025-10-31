package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsAndroid
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid.Name
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

internal object AssetsModuleAndroid {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            factory<AssetsProtocol> {
                AssetsAndroid(
                    context = get(Name.APPLICATION_CONTEXT),
                )
            }
        }
    }

}