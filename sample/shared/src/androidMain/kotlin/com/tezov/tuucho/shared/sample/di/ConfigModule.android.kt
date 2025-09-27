package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.barrel.di.SystemBarrelModules
import com.tezov.tuucho.sample.shared.BuildConfig
import org.koin.dsl.ModuleDeclaration

internal object ConfigModuleAndroid {

    fun invoke(): ModuleDeclaration = {
        single<SystemBarrelModules.Config> {
            object : SystemBarrelModules.Config {
                override val localDatabaseFile = BuildConfig.localDatabaseFile
                override val serverUrl = BuildConfig.serverUrl
            }
        }
    }

}