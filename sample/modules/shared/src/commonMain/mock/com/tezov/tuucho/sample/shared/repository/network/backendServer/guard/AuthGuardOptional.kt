package com.tezov.tuucho.sample.shared.repository.network.backendServer.guard

import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
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
                if (request.url.startsWith("auth") || request.headers[HttpHeaders.Authorization] != null) {
                    authGuard.allowed(version, request)
                } else {
                    true
                }
            }

            else -> throw Exception("unknown version $version")
        }
    }
}
