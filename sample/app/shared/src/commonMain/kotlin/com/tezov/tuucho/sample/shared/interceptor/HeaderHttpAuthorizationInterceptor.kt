package com.tezov.tuucho.sample.shared.interceptor

import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.flow.FlowCollector

class HeaderHttpAuthorizationInterceptor(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val config: NetworkModule.Config,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase
) : HttpInterceptor {

    private val authRegex = Regex("^/auth(?:/.*)?$")

    override suspend fun FlowCollector<HttpResponseData>.process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) {
        with(context.builder) {
            val route = url.toString()
                .removePrefix("${config.baseUrl}/")
                .removePrefix("${config.version}/")
                .let {
                    when {
                        it.startsWith(config.healthEndpoint) -> it.removePrefix(config.healthEndpoint)
                        it.startsWith(config.resourceEndpoint) -> it.removePrefix(config.resourceEndpoint)
                        it.startsWith(config.sendEndpoint) -> it.removePrefix(config.sendEndpoint)
                        it.startsWith(config.imageEndpoint) -> it.removePrefix(config.imageEndpoint)
                        else -> it
                    }
                }

            if (route.matches(authRegex)) {
                useCaseExecutor.await(
                    useCase = getValueOrNullFromStore,
                    input = GetValueOrNullFromStoreUseCase.Input(
                        key = "login-authorization".toKey()
                    )
                )?.value?.value?.let { authorizationKey ->
                    headers.append("authorization", "Bearer $authorizationKey")
                }
            }
            next?.invoke(context)
        }
    }
}
