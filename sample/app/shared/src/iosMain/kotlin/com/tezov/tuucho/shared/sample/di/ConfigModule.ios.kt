package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.sample.app.shared.BuildKonfig
import org.koin.dsl.ModuleDeclaration

internal object ConfigModuleIos {

    fun invoke(): ModuleDeclaration = {
        factory<SystemCoreDataModules.Config> {
            object : SystemCoreDataModules.Config {
                override val localDatabaseFile = BuildKonfig.localDatabaseFile
                override val localDatastoreFile = BuildKonfig.localDatastoreFile
                override val serverUrl = BuildKonfig.serverUrl
                override val serverConnectTimeoutMillis = BuildKonfig.serverConnectTimeoutMillis
                override val serverSocketTimeoutMillis = BuildKonfig.serverSocketTimeoutMillis
            }
        }
    }

}