package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.barrel.di.SystemBarrelModules
import org.koin.dsl.ModuleDeclaration

internal object ConfigModuleIos {

    fun invoke(): ModuleDeclaration = {
        single<SystemBarrelModules.Config> {
            object : SystemBarrelModules.Config {
                override val localDatabaseFile = "database.db"
                override val serverUrl = "http://127.0.0.1:3000"
            }
        }
    }

}