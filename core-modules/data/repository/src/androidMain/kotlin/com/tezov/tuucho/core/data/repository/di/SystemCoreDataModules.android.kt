package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.qualifier.named

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    DatabaseRepositoryModuleAndroid.invoke(),
    NetworkRepositoryModuleAndroid.invoke(),
    AssetsModuleAndroid.invoke(),
    StoreRepositoryModuleAndroid.invoke()
)

object SystemCoreDataModulesAndroid {
    object Name {
        val APPLICATION_CONTEXT = named("DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT")
    }
}
