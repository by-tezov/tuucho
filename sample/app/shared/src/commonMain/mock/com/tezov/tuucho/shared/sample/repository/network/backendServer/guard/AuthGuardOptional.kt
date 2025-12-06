package com.tezov.tuucho.shared.sample.repository.network.backendServer.guard

import com.tezov.tuucho.shared.sample.repository.network.backendServer.BackendServer
import com.tezov.tuucho.shared.sample.repository.network.backendServer.protocol.GuardProtocol
import io.ktor.http.HttpHeaders

internal class AuthGuardOptional(
    private val authGuard: AuthGuard
) : GuardProtocol {

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean {
        return when (version) {
            "v1" -> {
                request.headers[HttpHeaders.Authorization] ?: return true
                authGuard.allowed(version, request)
            }

            else -> throw Exception("unknown version $version")
        }
    }
}
