package com.tezov.tuucho.shared.sample.middleware

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.NextMiddleware
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ServerHealthCheckUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import kotlinx.coroutines.delay

class BeforeNavigateToUrlMiddleware(
    private val useCaseExecutor: UseCaseExecutor,
    private val serverHealthCheck: ServerHealthCheckUseCase,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase,
) : NavigationMiddleware.ToUrl {

    private companion object {
        const val LOGIN_AUTHORIZATION = "login-authorization"
        const val AUTH_PREFIX = "auth"
        const val LOBBY_CONFIG = "lobby/_config"
        const val AUTH_CONFIG = "auth/_config"
        const val LOGIN_PAGE = "lobby/page-login"
        const val HOME_PAGE = "auth/page-home"
    }

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>?,
    ) {
        if (ensureAuthorizationOrRedirectToLoginPage(context, next)) return
        if (autoLoginOnStart(context, next)) return
        loadLobbyConfigOnStart(context) || loadAuthConfigAfterSuccessfulLogin(context)
        next?.invoke(context.withExceptionHandler())
    }

    private suspend fun isAuthorizationExist() = useCaseExecutor.invokeSuspend(
        useCase = getValueOrNullFromStore,
        input = GetValueOrNullFromStoreUseCase.Input(
            key = LOGIN_AUTHORIZATION.toKey()
        )
    ).value?.value != null

    private suspend fun isAuthorizationValid() = runCatching {
        useCaseExecutor.invokeSuspend(
            useCase = serverHealthCheck,
            input = ServerHealthCheckUseCase.Input(
                url = AUTH_PREFIX
            )
        )
    }.getOrNull() != null

    private suspend fun loadLobbyConfig() {
        useCaseExecutor.invokeSuspend(
            useCase = refreshMaterialCache,
            input = RefreshMaterialCacheUseCase.Input(
                url = LOBBY_CONFIG
            )
        )
    }

    private suspend fun loadAuthConfig() {
        useCaseExecutor.invokeSuspend(
            useCase = refreshMaterialCache,
            input = RefreshMaterialCacheUseCase.Input(
                url = AUTH_CONFIG
            )
        )
    }

    private suspend fun ensureAuthorizationOrRedirectToLoginPage(
        context: NavigationMiddleware.ToUrl.Context,
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>?,
    ): Boolean {
        if (context.input.url.startsWith("$AUTH_PREFIX/")) {
            if (!isAuthorizationExist()) {
                next?.invoke(
                    context.withExceptionHandler()
                        .copy(
                            input = context.input.copy(url = LOGIN_PAGE)
                        )
                )
                return true
            }
        }
        return false
    }

    private suspend fun autoLoginOnStart(
        context: NavigationMiddleware.ToUrl.Context,
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>?,
    ): Boolean {
        if (context.currentUrl == null && context.input.url == LOGIN_PAGE && isAuthorizationExist() && isAuthorizationValid()) {
            loadAuthConfig()
            next?.invoke(
                context.withExceptionHandler()
                    .copy(
                        input = context.input.copy(url = HOME_PAGE)
                    )
            )
            return true
        }
        return false
    }

    private suspend fun loadLobbyConfigOnStart(
        context: NavigationMiddleware.ToUrl.Context,
    ): Boolean {
        if (context.currentUrl == null && context.input.url == LOGIN_PAGE) {
            loadLobbyConfig()
            return true
        }
        return false
    }

    private suspend fun loadAuthConfigAfterSuccessfulLogin(
        context: NavigationMiddleware.ToUrl.Context,
    ): Boolean {
        if (context.currentUrl == LOGIN_PAGE && context.input.url == HOME_PAGE) {
            loadAuthConfig()
            return true
        }
        return false
    }

    //IMPROVE: need to find a better pattern to react on shadower exception (sync, async)
    private fun NavigationMiddleware.ToUrl.Context.withExceptionHandler(): NavigationMiddleware.ToUrl.Context {
        return this.copy(
            onShadowerException = { exception, context, replay ->
                val maxRetries = 3
                var failure: Throwable? = exception
                for (attempt in 0 until maxRetries) {
                    val delayMs = (1000L * (1 shl attempt)).coerceAtMost(5000L)
                    delay(delayMs)

                    val result = runCatching { replay.invoke() }
                    failure = result.exceptionOrNull()
                    if (failure == null) {
                        break
                    }
                }
                // TODO: handle permanent failure (e.g. show error screen or fallback)
                failure?.let { throw failure }
            })
    }
}

