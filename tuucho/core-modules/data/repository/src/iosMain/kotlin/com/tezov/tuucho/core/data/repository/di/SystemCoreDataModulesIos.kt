package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<KoinMass> = listOf(
    PlatformModuleIos.invoke(),
    DatabaseModuleIos.invoke(),
    NetworkModuleIos.invoke(),
    ImageModuleIos.invoke(),
    AssetModuleIos.invoke(),
    StoreRepositoryModuleIos.invoke()
)
