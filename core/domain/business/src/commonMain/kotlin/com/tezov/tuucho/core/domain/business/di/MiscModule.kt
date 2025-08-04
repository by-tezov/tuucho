package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepository
import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepository
import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object MiscModule {

    @OptIn(ExperimentalSerializationApi::class)
    internal operator fun invoke() = module {
        single<Json> {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = true
            }
        }

        single<NavigationStackRepositoryProtocol> {
            NavigationStackRepository(
                coroutineScopes = get()
            )
        }
        single<ViewContextStackRepositoryProtocol> {
            ViewContextStackRepository(
                coroutineScopes = get()
            )
        }


    }

}


