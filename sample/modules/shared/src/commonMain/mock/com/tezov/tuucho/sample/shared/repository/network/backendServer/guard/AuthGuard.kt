package com.tezov.tuucho.sample.shared.repository.network.backendServer.guard

import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.store.LoginTokenStore
import io.ktor.http.HttpHeaders

internal class AuthGuard(
    private val loginTokenStore: LoginTokenStore
) : GuardProtocol {

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean = when (version) {
        "v1" -> {
            val headerAuth = request.headers[HttpHeaders.Authorization]
                ?: return false

            val parts = headerAuth.trim().split(Regex("\\s+"))
            val token = parts
                .takeIf { it.size == 2 && it[0].equals("bearer", ignoreCase = true) }
                ?.get(1)
                ?: return false

            loginTokenStore.isValid(token)
        }

        else -> throw Exception("unknown version $version")
    }
}
