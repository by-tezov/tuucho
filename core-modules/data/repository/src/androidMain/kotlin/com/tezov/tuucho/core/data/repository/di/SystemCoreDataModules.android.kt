package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    DatabaseRepositoryModuleAndroid.invoke(),
    NetworkRepositoryModuleAndroid.invoke(),
    AssetsModuleAndroid.invoke(),
    StoreRepositoryModuleAndroid.invoke()
)
