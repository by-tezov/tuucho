package com.tezov.tuucho.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

object ApplicationModule {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupCore.Main

        override fun Module.declaration() {

        }

    }

}
