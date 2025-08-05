package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.navigation.NavigationDestinationStackRepository
import com.tezov.tuucho.core.domain.business.navigation.NavigationScreenStackProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
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

        single<NavigationRepositoryProtocol.Destination> {
            NavigationDestinationStackRepository(
                coroutineScopes = get(),
            )
        }
        single<NavigationRepositoryProtocol.StackScreen> {
            NavigationScreenStackProtocol(
                coroutineScopes = get(),
                screenRenderer = get()
            )
        }


    }

}


