package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import com.tezov.tuucho.sample.app.shared.BuildKonfig
import com.tezov.tuucho.shared.sample.di.NetworkModule
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.encodedPath

class HeadersInterceptor(
    private val config: NetworkModule.Config,
) : NetworkRepositoryModule.RequestInterceptor {

    override suspend fun intercept(builder: HttpRequestBuilder) {
        builder.headers.append("platform", config.headerPlatform)
    }
}