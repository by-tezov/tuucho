package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    DatabaseRepositoryModuleJvm.invoke(),
    NetworkRepositoryModuleJvm.invoke(),
    AssetsModuleJvm.invoke(),
    StoreRepositoryModuleJvm.invoke()
)

object SystemCoreDataModulesJvm {
    interface Config {
        val liveRelativeFolderPath: String
    }
}
