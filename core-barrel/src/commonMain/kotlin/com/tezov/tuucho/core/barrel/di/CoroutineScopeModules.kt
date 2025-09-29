package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.barrel._system.CoroutineScopes
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import org.koin.dsl.module

internal object CoroutineScopeModules {

    fun invoke() = module {
        single<CoroutineScopesProtocol> { CoroutineScopes() }
    }
}