package com.tezov.tuucho.kmm.di

import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import com.tezov.tuucho.kmm._system.CoroutineScopeProvider
import org.koin.dsl.module

object SystemKmmModules {

    internal operator fun invoke() = listOf(
        ViewModelModule.invoke(),
        module {

            single<CoroutineScopeProviderProtocol> {
                CoroutineScopeProvider()
            }
        }
    )
}