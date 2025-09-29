package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.di.SystemCoreDataModules
import com.tezov.tuucho.sample.app.shared.BuildKonfig
import org.koin.dsl.ModuleDeclaration

internal object ConfigModuleIos {

    fun invoke(): ModuleDeclaration = {
        single<SystemCoreDataModules.Config> {
            object : SystemCoreDataModules.Config {
                override val localDatabaseFile = BuildKonfig.localDatabaseFile
                override val serverUrl = BuildKonfig.serverUrl
            }
        }
    }

}