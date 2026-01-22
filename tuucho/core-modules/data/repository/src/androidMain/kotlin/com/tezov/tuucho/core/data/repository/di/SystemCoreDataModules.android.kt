package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemInformationAndroid
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryAndroid
import com.tezov.tuucho.core.data.repository.repository.SystemInformation
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<KoinMass> = listOf(
    module(ModuleContextData.Main) {
        factoryOf(::SystemInformationAndroid) bind SystemInformation.PlatformProtocol::class
        factoryOf(::ReferenceFactoryAndroid) bind ReferenceProtocol.Factory::class
    },
    DatabaseModuleAndroid.invoke(),
    NetworkModuleAndroid.invoke(),
    ImageModuleAndroid.invoke(),
    AssetModuleAndroid.invoke(),
    StoreRepositoryModuleAndroid.invoke()
)

object SystemCoreDataModulesAndroid {
    object Name {
        val APPLICATION_CONTEXT get() = named("DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT")
    }
}
