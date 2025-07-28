package com.tezov.tuucho.kmm.system.di

import android.content.Context
import com.tezov.tuucho.core.data.di.DatabaseRepositoryModuleAndroid
import org.koin.dsl.module

object MiscModuleAndroid {

    internal operator fun invoke(
        applicationContext: Context
    ) = module {

        single<Context>(DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }

    }
}