package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    module(ModuleGroupData.Main) {
        factory<SystemRepository.PlatformProtocol> {
            SystemRepositoryIos()
        }
    },
    DatabaseRepositoryModuleIos.invoke(),
    NetworkRepositoryModuleIos.invoke(),
    AssetsModuleIos.invoke(),
    StoreRepositoryModuleIos.invoke()
)
