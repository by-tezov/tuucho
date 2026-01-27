package com.tezov.tuucho.core.data.repository.image

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import org.koin.core.qualifier.named

interface ImageFetcherRegistryProtocol {
    fun register(
        name: String
    )

    fun get(
        name: String
    ): ImageFetcherProtocol.Factory
}

internal class ImageFetcherRegistry :
    ImageFetcherRegistryProtocol,
    TuuchoKoinComponent {
    private val registry = mutableMapOf<String, ImageFetcherProtocol.Factory>()

    override fun register(
        name: String
    ) {
        if (registry.containsKey(name)) {
            throw DataException.Default("Image fetcher with name $name already exists")
        }
        registry[name] = getKoin().get(named(name))
    }

    override fun get(
        name: String
    ) = registry[name] ?: throw DataException.Default("Image fetcher with name $name not found")
}
