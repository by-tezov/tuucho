package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemInformation
import com.tezov.tuucho.core.data.repository._system.SystemInformationIos
import com.tezov.tuucho.core.data.repository._system.SystemPlatform
import com.tezov.tuucho.core.data.repository._system.SystemPlatformIos
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryIos
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<KoinMass> = listOf(
    module(ModuleContextData.Main) {
        factory<SystemInformationIos>() bind SystemInformation.PlatformProtocol::class
        factory<ReferenceFactoryIos>() bind ReferenceProtocol.Factory::class
        factory<SystemPlatformIos>() bind SystemPlatform::class
    },
    DatabaseModuleIos.invoke(),
    NetworkModuleIos.invoke(),
    ImageModuleIos.invoke(),
    AssetModuleIos.invoke(),
    StoreRepositoryModuleIos.invoke()
)
