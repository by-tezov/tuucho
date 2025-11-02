package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsAndroid
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.module.Module

internal object AssetsModuleAndroid {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            @OptIn(TuuchoInternalApi::class)
            factory<AssetsProtocol> {
                AssetsAndroid(
                    context = get(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT),
                )
            }
        }
    }
}
