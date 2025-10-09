package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.GeyValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import io.ktor.client.request.HttpRequestBuilder
import org.koin.dsl.ModuleDeclaration

object NetworkRepositoryModule {

    fun invoke(): ModuleDeclaration = {
        factory<NetworkRepositoryModule.KtorRequestInterceptor> {
            object : NetworkRepositoryModule.KtorRequestInterceptor {
                private val useCaseExecutor = get<UseCaseExecutor>()
                private val geyValueOrNullFromStore = get<GeyValueOrNullFromStoreUseCase>()

                override suspend fun intercept(builder: HttpRequestBuilder) {
                    val route = builder.url.parameters["url"] ?: return
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