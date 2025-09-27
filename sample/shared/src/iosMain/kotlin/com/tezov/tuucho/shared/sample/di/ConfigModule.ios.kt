package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.barrel.di.SystemBarrelModules
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

internal object ConfigModuleIos {

    fun invoke(): ModuleDeclaration = {
        single<SystemBarrelModules.Config> {
            object : SystemBarrelModules.Config {
                override val localDatabaseFile = //TODO
                override val serverUrl = //TODO
            }
        }
    }

}