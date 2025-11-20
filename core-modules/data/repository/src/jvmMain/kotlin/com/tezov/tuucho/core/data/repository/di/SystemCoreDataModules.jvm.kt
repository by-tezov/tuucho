package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemInformationJvm
import com.tezov.tuucho.core.data.repository.repository.SystemInformation
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol> = listOf(
    module(ModuleGroupData.Main) {
        factory<SystemInformation.PlatformProtocol> {
            SystemInformationJvm()
        }
    },
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
