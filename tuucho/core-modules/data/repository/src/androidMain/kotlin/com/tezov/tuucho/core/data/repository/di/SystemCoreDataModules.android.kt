package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import com.tezov.tuucho.core.data.repository._system.Platform
import com.tezov.tuucho.core.data.repository._system.SystemInformationAndroid
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryAndroid
import com.tezov.tuucho.core.data.repository.repository.SystemInformation
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import java.io.File

@OptIn(TuuchoInternalApi::class)
internal actual fun SystemCoreDataModules.platformInvoke(): List<KoinMass> = listOf(
    module(ModuleContextData.Main) {
        factoryOf(::SystemInformationAndroid) bind SystemInformation.PlatformProtocol::class
        factoryOf(::ReferenceFactoryAndroid) bind ReferenceProtocol.Factory::class
        factory<Platform> {
            object : Platform {
                val context = get<Context>(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT)
                override fun pathFromCacheFolder(relativePath: String): Path {
                    return File(context.cacheDir.path, relativePath).toOkioPath()
                }
            }
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
