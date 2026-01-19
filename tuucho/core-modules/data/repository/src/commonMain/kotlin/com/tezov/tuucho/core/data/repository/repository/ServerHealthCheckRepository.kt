package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.HealthCheckSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ServerHealthCheckRepositoryProtocol

internal class ServerHealthCheckRepository(
    private val healthCheckSource: HealthCheckSource
) : ServerHealthCheckRepositoryProtocol {

    override suspend fun process(url: String) = healthCheckSource.process(url)
}
