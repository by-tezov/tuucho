package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.barrel.di.SystemBarrelModules
import com.tezov.tuucho.sample.shared.BuildKonfig
import org.koin.dsl.ModuleDeclaration

internal object ConfigModuleAndroid {

    fun invoke(): ModuleDeclaration = {
        single<SystemBarrelModules.Config> {
            object : SystemBarrelModules.Config {
                override val localDatabaseFile = BuildKonfig.localDatabaseFile
                override val serverUrl = BuildKonfig.serverUrl
            }
        }
    }

}