package com.tezov.tuucho.core.data.di

import org.koin.dsl.module

object SystemCoreDataModules {

    interface Config {
        val localDatabaseFile: String
        val serverUrl: String
    }

    internal fun invoke() = module {
        factory<com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules.Config> {
            object : com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules.Config {
                val config = get<Config>()
                override val localDatabaseFile = config.localDatabaseFile
                override val serverUrl = config.serverUrl
            }
        }
    }
}