package com.tezov.tuucho.shared.sample.hooks

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.HookProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.GeyValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.ServerHealthCheckUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.coroutines.delay

//TODO super high dirty, it works, now need to make a clean stuff

class BeforeNavigateToUrlHook(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val serverHealthCheck: ServerHealthCheckUseCase,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val geyValueOrNullFromStore: GeyValueOrNullFromStoreUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : HookProtocol.BeforeNavigateToUrl {
    override suspend fun onEvent(
        currentUrl: String?,
        nextUrl: String,
    ): Boolean {
        if (nextUrl.startsWith("auth/")) {
            val loginAuthorization = useCaseExecutor.invokeSuspend(
                useCase = geyValueOrNullFromStore,
                input = GeyValueOrNullFromStoreUseCase.Input(
                    key = "login-authorization".toKey()
                )
            ).value?.value
            if (loginAuthorization == null) {
                coroutineScopes.navigation.async {
                    useCaseExecutor.invoke(
                        useCase = navigateToUrl,
                        input = NavigateToUrlUseCase.Input(
                            url = "lobby/page-login"
                        )
                    )
                }
                return false
            }
        }
        if (currentUrl == null && nextUrl == "lobby/page-login") {
            runCatching {
                useCaseExecutor.invokeSuspend(
                    useCase = serverHealthCheck,
                    input = ServerHealthCheckUseCase.Input(
                        url = "auth"
                    )
                )
            }.onFailure {
                useCaseExecutor.invokeSuspend(
                    useCase = refreshMaterialCache,
                    input = RefreshMaterialCacheUseCase.Input(
                        url = "lobby/_config"
                    )
                )
            }.onSuccess {
                val loginAuthorization = useCaseExecutor.invokeSuspend(
                    useCase = geyValueOrNullFromStore,
                    input = GeyValueOrNullFromStoreUseCase.Input(
                        key = "login-authorization".toKey()
                    )
                ).value?.value
                if (loginAuthorization != null) {
                    coroutineScopes.navigation.async {
                        useCaseExecutor.invoke(
                            useCase = navigateToUrl,
                            input = NavigateToUrlUseCase.Input(
                                url = "auth/page-home"
                            )
                        )
                    }
                    return false
                }
                else {
                    useCaseExecutor.invokeSuspend(
                        useCase = refreshMaterialCache,
                        input = RefreshMaterialCacheUseCase.Input(
                            url = "lobby/_config"
                        )
                    )
                }
            }


        } else if (currentUrl == "lobby/page-login" && nextUrl == "auth/page-home") {
            useCaseExecutor.invokeSuspend(
                useCase = refreshMaterialCache,
                input = RefreshMaterialCacheUseCase.Input(
                    url = "auth/_config"
                )
            )
        }

        return true
    }
}