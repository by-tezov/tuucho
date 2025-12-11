package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModulesAndroid
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module

object ApplicationModule {

    fun invoke(
        applicationContext: Context
    ) = module(ModuleGroupCore.Main) {
        factory<Context>(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }
    }

}
