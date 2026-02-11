package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.barrel._system.CoroutineScopes
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import org.koin.dsl.bind
import org.koin.dsl.onClose
import org.koin.plugin.module.dsl.single

internal object CoroutineScopeModules {
    fun invoke() = module(ModuleContextCore.Main) {
        single<CoroutineScopes>() bind CoroutineScopesProtocol::class onClose { coroutineScopes ->
            coroutineScopes?.cancel()
        }
    }
}
