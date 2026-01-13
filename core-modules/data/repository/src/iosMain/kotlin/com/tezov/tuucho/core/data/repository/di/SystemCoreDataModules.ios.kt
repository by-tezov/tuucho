package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemInformationIos
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryIos
import com.tezov.tuucho.core.data.repository.repository.SystemInformation
import com.tezov.tuucho.core.domain.business.di.Koin
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.dsl.bind

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<Koin> = listOf(
    module(ModuleGroupData.Main) {
        factory<SystemInformation.PlatformProtocol> {
            SystemInformationIos()
        }
        factory {
            ReferenceFactoryIos()
        } bind ReferenceProtocol.Factory::class
    },
    DatabaseRepositoryModuleIos.invoke(),
    NetworkRepositoryModuleIos.invoke(),
    AssetsModuleIos.invoke(),
    StoreRepositoryModuleIos.invoke()
)
