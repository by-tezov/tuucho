package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

internal object AssetsModuleAndroid {
    fun invoke() = module(ModuleGroupData.Main) {
        @OptIn(TuuchoInternalApi::class)
        factory<AssetsProtocol> {
            _root_ide_package_.com.tezov.tuucho.core.data.repository.assets.AssetsAndroid(
                context = get(
                    _root_ide_package_.com.tezov.tuucho.core.data.repository.di.SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT
                ),
            )
        }
    }
}
