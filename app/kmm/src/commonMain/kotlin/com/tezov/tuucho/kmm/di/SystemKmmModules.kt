package com.tezov.tuucho.kmm.di

import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.kmm._system.CoroutineScopes
import org.koin.dsl.module

object SystemKmmModules {

    internal operator fun invoke() = listOf(
        ViewModelModule.invoke(),
        module {

            single<CoroutineScopesProtocol> {
                CoroutineScopes()
            }
        }
    )
}