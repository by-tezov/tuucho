package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.barrel._system.CoroutineScopes
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module

internal object CoroutineScopeModules {
    fun invoke() = module(ModuleGroupCore.Main) {
        single<CoroutineScopesProtocol> { CoroutineScopes() }
    }
}
