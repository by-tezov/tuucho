package com.tezov.tuucho.sample.shared.middleware.navigateToUrl

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ServerHealthCheckUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import com.tezov.tuucho.sample.shared._system.Page

class BeforeNavigateToUrlMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val serverHealthCheck: ServerHealthCheckUseCase,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase,
) : NavigationMiddleware.ToUrl {

    private companion object {
        const val LOGIN_AUTHORIZATION = "login-authorization"
        const val AUTH_PREFIX = "auth"
        const val LOBBY_CONFIG = "lobby/_config"
        const val AUTH_CONFIG = "auth/_config"
    }

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context>?,
    ) {
        if (ensureAuthorizationOrRedirectToLoginPage(context, next)) return
        if (autoLoginOnStart(context, next)) return
        loadLobbyConfigOnStart(context) || loadAuthConfigAfterSuccessfulLogin(context)
        next?.invoke(context)
    }

    private suspend fun isAuthorizationExist() = useCaseExecutor.await(
        useCase = getValueOrNullFromStore,
        input = GetValueOrNullFromStoreUseCase.Input(
            key = LOGIN_AUTHORIZATION.toKey()
        )
    )?.value?.value != null

    private suspend fun isAuthorizationValid() = runCatching {
        useCaseExecutor.await(
            useCase = serverHealthCheck,
            input = ServerHealthCheckUseCase.Input(
                url = AUTH_PREFIX
            )
        )
    }.getOrNull() != null

    private suspend fun loadLobbyConfig() {
        useCaseExecutor.await(
            useCase = refreshMaterialCache,
            input = RefreshMaterialCacheUseCase.Input(
                url = LOBBY_CONFIG
            )
        )
    }

    private suspend fun loadAuthConfig() {
        useCaseExecutor.await(
            useCase = refreshMaterialCache,
            input = RefreshMaterialCacheUseCase.Input(
                url = AUTH_CONFIG
            )
        )
    }

    private suspend fun ensureAuthorizationOrRedirectToLoginPage(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context>?,
    ): Boolean {
        if (context.input.url.startsWith("$AUTH_PREFIX/")) {
            if (!isAuthorizationExist()) {
                next?.invoke(
                    context.copy(input = context.input.copy(url = Page.login))
                )
                return true
            }
        }
        return false
    }

    private suspend fun autoLoginOnStart(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context>?,
    ): Boolean {
        if (context.currentUrl == null && context.input.url == Page.login && isAuthorizationExist() && isAuthorizationValid()) {
            loadAuthConfig()
            next?.invoke(
                context.copy(input = context.input.copy(url = Page.home))
            )
            return true
        }
        return false
    }

    private suspend fun loadLobbyConfigOnStart(
        context: NavigationMiddleware.ToUrl.Context,
    ): Boolean {
        if (context.currentUrl == null && context.input.url == Page.login) {
            loadLobbyConfig()
            return true
        }
        return false
    }

    private suspend fun loadAuthConfigAfterSuccessfulLogin(
        context: NavigationMiddleware.ToUrl.Context,
    ): Boolean {
        if (context.currentUrl == Page.login && context.input.url == Page.home) {
            loadAuthConfig()
            return true
        }
        return false
    }
}

