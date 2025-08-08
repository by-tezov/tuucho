package com.tezov.tuucho.kmm.di

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.kmm._system.CoroutineScopes
import org.koin.dsl.module

object SystemKmmModules {

    internal operator fun invoke() = listOf(
        module {
            single<CoroutineScopesProtocol> {
                CoroutineScopes()
            }
        }
    )
}