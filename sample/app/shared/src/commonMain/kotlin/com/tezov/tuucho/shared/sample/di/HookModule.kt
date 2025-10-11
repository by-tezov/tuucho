package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business.protocol.HookProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.GeyValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.shared.sample.hooks.BeforeNavigateToUrlHook
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.encodedPath
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

object HookModule {

    fun invoke(): ModuleDeclaration = {

        factory<BeforeNavigateToUrlHook> {
            BeforeNavigateToUrlHook(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                geyValueOrNullFromStore = get(),
                navigateToUrl = get(),
            )
        } bind HookProtocol.BeforeNavigateToUrl::class
    }
}