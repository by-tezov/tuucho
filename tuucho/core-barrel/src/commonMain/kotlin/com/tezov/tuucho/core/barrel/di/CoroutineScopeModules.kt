package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.barrel._system.CoroutineScopes
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal object CoroutineScopeModules {
    fun invoke() = module(ModuleContextCore.Main) {
        singleOf(::CoroutineScopes) bind CoroutineScopesProtocol::class
    }
}
