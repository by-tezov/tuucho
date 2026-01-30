package com.tezov.tuucho.core.data.repository.image

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent

interface ImageFetcherRegistryProtocol {
    fun get(
        name: String
    ): ImageFetcherProtocol.Factory
}

internal class ImageFetcherRegistry(
    factories: List<ImageFetcherProtocol.Factory>
) : ImageFetcherRegistryProtocol,
    TuuchoKoinComponent {
    private val registry = buildMap {
        factories.forEach { put(it.command, it) }
    }

    override fun get(
        name: String
    ) = registry[name] ?: throw DataException.Default("Image fetcher with name $name not found")
}
