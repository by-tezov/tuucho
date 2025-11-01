package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.barrel._system.CoroutineScopes
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

internal object CoroutineScopeModules {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupCore.Main

        override fun Module.declaration() {
            single<CoroutineScopesProtocol> { CoroutineScopes() }
        }
    }
}
