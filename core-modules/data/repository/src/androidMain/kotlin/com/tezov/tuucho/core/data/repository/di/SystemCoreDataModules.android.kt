package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    DatabaseRepositoryModuleAndroid.invoke(),
    NetworkRepositoryModuleAndroid.invoke(),
    AssetsModuleAndroid.invoke(),
    StoreRepositoryModuleAndroid.invoke()
)
