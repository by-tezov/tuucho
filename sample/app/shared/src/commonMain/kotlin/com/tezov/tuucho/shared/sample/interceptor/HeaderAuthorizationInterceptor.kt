package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.encodedPath

class HeaderAuthorizationInterceptor(
    private val useCaseExecutor: UseCaseExecutor,
    private val config: NetworkRepositoryModule.Config,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase
) : NetworkRepositoryModule.RequestInterceptor {

    private val authRegex = Regex("^/auth(?:/.*)?$")

    override suspend fun intercept(builder: HttpRequestBuilder) {
        val route = builder.url.toString()
            .removePrefix("${config.baseUrl}/")
            .removePrefix("${config.version}/")
            .let {
                when  {
                    it.startsWith(config.healthEndpoint) -> it.removePrefix(config.healthEndpoint)
                    it.startsWith(config.resourceEndpoint) -> it.removePrefix(config.resourceEndpoint)
                    it.startsWith(config.sendEndpoint) -> it.removePrefix(config.sendEndpoint)
                    else -> it
                }
            }
        if (!route.matches(authRegex)) return
        useCaseExecutor.invokeSuspend(
            useCase = getValueOrNullFromStore,
            input = GetValueOrNullFromStoreUseCase.Input(
                key = "login-authorization".toKey()
            )
        ).value?.value?.let { authorizationKey ->
            builder.headers.append("Authorization", "Bearer $authorizationKey")
        }
    }
}