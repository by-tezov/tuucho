package com.tezov.tuucho.shared.sample.hooks

import com.tezov.tuucho.core.domain.business.protocol.HookProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.GetValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.ServerHealthCheckUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class BeforeNavigateToUrlHook(
    private val useCaseExecutor: UseCaseExecutor,
    private val serverHealthCheck: ServerHealthCheckUseCase,
    private val refreshMaterialCache: RefreshMaterialCacheUseCase,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase,
    private val navigateToUrl: NavigateToUrlUseCase,
) : HookProtocol.BeforeNavigateToUrl {

    private companion object {
        const val LOGIN_AUTHORIZATION = "login-authorization"
        const val AUTH_PREFIX = "auth"
        const val LOBBY_CONFIG = "lobby/_config"
        const val AUTH_CONFIG = "auth/_config"
        const val LOGIN_PAGE = "lobby/page-login"
        const val HOME_PAGE = "auth/page-home"
    }

    override suspend fun onEvent(
        currentUrl: String?,
        nextUrl: String,
    ): Boolean {
        if (!ensureAuthorizationOrRedirectToLoginPage(nextUrl)) return false
        if (!autoLoginOnStart(currentUrl, nextUrl)) return false
        if (loadLobbyConfigOnStart(currentUrl, nextUrl)) return true
        loadAuthConfigAfterSuccessfulLogin(currentUrl, nextUrl)
        return true
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

    private fun navigateToHomePage() {
        useCaseExecutor.invoke(
            useCase = navigateToUrl,
            input = NavigateToUrlUseCase.Input(
                url = HOME_PAGE
            )
        )
    }

    private fun navigateToLoginPage() {
        useCaseExecutor.invoke(
            useCase = navigateToUrl,
            input = NavigateToUrlUseCase.Input(
                url = LOGIN_PAGE
            )
        )
    }

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

    private suspend fun ensureAuthorizationOrRedirectToLoginPage(nextUrl: String): Boolean {
        if (nextUrl.startsWith("$AUTH_PREFIX/")) {
            if (!isAuthorizationExist()) {
                navigateToLoginPage()
                return false
            }
        }
        return true
    }

    private suspend fun autoLoginOnStart(
        currentUrl: String?,
        nextUrl: String,
    ): Boolean {
        if (currentUrl == null && nextUrl == LOGIN_PAGE && isAuthorizationExist() && isAuthorizationValid()) {
            loadAuthConfig()
            navigateToHomePage()
            return false
        }
        return true
    }

    private suspend fun loadLobbyConfigOnStart(
        currentUrl: String?,
        nextUrl: String,
    ): Boolean {
        if (currentUrl == null && nextUrl == LOGIN_PAGE) {
            loadLobbyConfig()
            return true
        }
        return false
    }

    private suspend fun loadAuthConfigAfterSuccessfulLogin(
        currentUrl: String?,
        nextUrl: String,
    ) {
        if (currentUrl == LOGIN_PAGE && nextUrl == HOME_PAGE) {
            loadAuthConfig()
        }
    }
}