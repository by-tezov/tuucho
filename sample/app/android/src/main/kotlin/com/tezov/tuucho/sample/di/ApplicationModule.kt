package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.data.di.ApplicationModules
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

object ApplicationModule {

    fun invoke(
        applicationContext: Context
    ) = object : ModuleProtocol {

        override val group = ModuleGroupCore.Main

        override fun Module.declaration() {
            single<Context>(ApplicationModules.Name.APPLICATION_CONTEXT) {
                applicationContext
            }
        }

    }

}