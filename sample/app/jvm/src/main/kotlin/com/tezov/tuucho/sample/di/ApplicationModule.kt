package com.tezov.tuucho.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.core.module.Module

object ApplicationModule {

    fun invoke() = module(ModuleGroupCore.Main) {


    }

}
