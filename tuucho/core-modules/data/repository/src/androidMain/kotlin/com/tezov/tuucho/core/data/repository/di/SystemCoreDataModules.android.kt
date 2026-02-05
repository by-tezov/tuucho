package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import com.tezov.tuucho.core.data.repository._system.SystemInformation
import com.tezov.tuucho.core.data.repository._system.SystemInformationAndroid
import com.tezov.tuucho.core.data.repository._system.SystemPlatform
import com.tezov.tuucho.core.data.repository._system.SystemPlatformAndroid
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryAndroid
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<KoinMass> = listOf(
    module(ModuleContextData.Main) {
        factory<SystemInformationAndroid>() bind SystemInformation.PlatformProtocol::class
        factory<ReferenceFactoryAndroid>() bind ReferenceProtocol.Factory::class
        factory<SystemPlatform> {
            SystemPlatformAndroid(
                context = get<Context>(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT)
            )
        }
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
