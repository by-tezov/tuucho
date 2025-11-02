package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModulesAndroid
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

object ApplicationModule {

    fun invoke(
        applicationContext: Context
    ) = object : ModuleProtocol {

        override val group = ModuleGroupCore.Main

        override fun Module.declaration() {
            single<Context>(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT) {
                applicationContext
            }
        }

    }

}
