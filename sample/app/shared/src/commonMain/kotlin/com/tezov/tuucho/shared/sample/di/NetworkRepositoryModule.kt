package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.GeyValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.encodedPath
import org.koin.dsl.ModuleDeclaration

object NetworkRepositoryModule {

    fun invoke(): ModuleDeclaration = {
        factory<NetworkRepositoryModule.RequestInterceptor> {
            object : NetworkRepositoryModule.RequestInterceptor {

                private val config = get<NetworkRepositoryModule.Config>()
                private val useCaseExecutor = get<UseCaseExecutor>()
                private val geyValueOrNullFromStore = get<GeyValueOrNullFromStoreUseCase>()

                override suspend fun intercept(builder: HttpRequestBuilder) {
                    val route = builder.url
                        .encodedPath
                        .removePrefix("/${config.version}/")
                        .let {
                            when  {
                                it.startsWith(config.resourceEndpoint) -> it.removePrefix("${config.resourceEndpoint}/")
                                it.startsWith(config.sendEndpoint) -> it.removePrefix("${config.sendEndpoint}/")
                                else -> it
                            }
                        }
                    if (!route.startsWith("auth/")) return
                    useCaseExecutor.invokeSuspend(
                        useCase = geyValueOrNullFromStore,
                        input = GeyValueOrNullFromStoreUseCase.Input(
                            key = "login-authorization".toKey()
                        )
                    ).value?.value?.let { authorizationKey ->
                        builder.headers.append("Authorization", "Bearer $authorizationKey")
                    }
                }
            }
        }
    }
}