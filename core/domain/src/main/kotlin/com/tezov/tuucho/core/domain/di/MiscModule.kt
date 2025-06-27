package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersImpl
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object MiscModule {

    internal operator fun invoke() = module {
        single<Json> {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = true
            }
        }

        single<CoroutineDispatchersProtocol> { CoroutineDispatchersImpl() }

        single {
            NavigationUrlActionHandler(
                get<CoroutineDispatchersProtocol>()
            )
        }
    }

}


