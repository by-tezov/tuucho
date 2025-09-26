package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid
import com.tezov.tuucho.platform.di.SystemPlatformModules
import com.tezov.tuucho.sample.android.BuildConfig
import org.koin.dsl.ModuleDeclaration

object ApplicationModuleDeclaration {

    operator fun invoke(
        applicationContext: Context,
    ): ModuleDeclaration = {
        single<Context>(DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }

        single<SystemPlatformModules.Config> {
            object : SystemPlatformModules.Config {
                override val localDatabaseFile = "something"
                override val serverUrl = "something"
            }
        }
    }

}