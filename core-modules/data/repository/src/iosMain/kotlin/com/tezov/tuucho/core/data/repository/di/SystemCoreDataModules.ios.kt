package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    DatabaseRepositoryModuleIos.invoke(),
    NetworkRepositoryModuleIos.invoke(),
    AssetsModuleIos.invoke(),
    StoreRepositoryModuleIos.invoke()
)