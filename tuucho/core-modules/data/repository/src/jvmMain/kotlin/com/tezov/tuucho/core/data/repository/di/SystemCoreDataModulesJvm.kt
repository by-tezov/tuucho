package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<KoinMass> = listOf(
    PlatformModuleJvm.invoke(),
    DatabaseModuleJvm.invoke(),
    NetworkModuleJvm.invoke(),
    ImageModuleJvm.invoke(),
    AssetModuleJvm.invoke(),
    StoreRepositoryModuleJvm.invoke()
)
