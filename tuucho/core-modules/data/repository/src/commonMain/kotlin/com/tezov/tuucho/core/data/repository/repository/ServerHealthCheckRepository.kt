package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.RemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ServerHealthCheckRepositoryProtocol

internal class ServerHealthCheckRepository(
    private val remoteSource: RemoteSource,
) : ServerHealthCheckRepositoryProtocol {
    override suspend fun process(
        url: String
    ) = remoteSource.healthCheck(url)
}
