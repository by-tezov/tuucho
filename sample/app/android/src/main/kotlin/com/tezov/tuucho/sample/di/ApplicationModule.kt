package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModulesAndroid
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module

object ApplicationModule {

    fun invoke(
        applicationContext: Context
    ) = module(ModuleContextCore.Main) {
        factory<Context>(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }
    }

}
