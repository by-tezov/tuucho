package com.tezov.tuucho.data.platform.di

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.data.platform._system.CoroutineScopes
import org.koin.dsl.module

object SystemPlatformModules {

    internal operator fun invoke() = listOf(
        module {
            single<CoroutineScopesProtocol> {
                CoroutineScopes()
            }
        }
    )
}