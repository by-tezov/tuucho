package com.tezov.tuucho.platform.di

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.platform._system.CoroutineScopes
import org.koin.dsl.module

object SystemPlatformModules {

    fun invoke() = listOf(
        module {
            single<CoroutineScopesProtocol> {
                CoroutineScopes()
            }
        }
    )
}