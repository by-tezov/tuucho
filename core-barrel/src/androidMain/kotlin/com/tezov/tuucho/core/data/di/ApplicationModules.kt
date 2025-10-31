package com.tezov.tuucho.core.data.di

import android.content.Context
import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named

object ApplicationModules {

    object Name {
        val APPLICATION_CONTEXT = named("ApplicationModules.Name.APPLICATION_CONTEXT")
    }

    internal fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupCore.Main

        override fun Module.declaration() {
            factory<Context>(DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT) {
                get(Name.APPLICATION_CONTEXT)
            }
        }
    }
}