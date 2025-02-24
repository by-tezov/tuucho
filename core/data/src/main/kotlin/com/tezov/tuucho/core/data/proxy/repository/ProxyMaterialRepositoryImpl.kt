package com.tezov.tuucho.core.data.proxy.repository

import com.tezov.tuucho.core.data.cache.parser.decoder.DecoderConfig
import com.tezov.tuucho.core.data.cache.repository.MaterialCacheRepository
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain
import com.tezov.tuucho.core.domain.repository.MaterialRepository

class ProxyMaterialRepositoryImpl(
    private val materialNetworkService: MaterialNetworkService,
    private val materialCacheRepository: MaterialCacheRepository,
) : MaterialRepository {

    override suspend fun refreshCache(url: String) {
        val configModelDomain = materialNetworkService.retrieveConfig(url)
        configModelDomain.preload.apply {
            subs
                .filter { materialCacheRepository.shouldRefresh(it.url, it.version) }
                .forEach { config ->
                    val adapterConfig = config.toAdapterConfig( true)
                    materialNetworkService.retrieve(config.url).let { materialElement ->
                        materialCacheRepository.refreshCache(adapterConfig, materialElement)
                    }
                }
            templates
                .filter { materialCacheRepository.shouldRefresh(it.url, it.version) }
                .forEach { config ->
                    val adapterConfig = config.toAdapterConfig(true)
                    materialNetworkService.retrieve(config.url).let { materialElement ->
                        materialCacheRepository.refreshCache(adapterConfig, materialElement)
                    }
                }
            pages
                .filter { materialCacheRepository.shouldRefresh(it.url, it.version) }
                .forEach { config ->
                    val adapterConfig = config.toAdapterConfig(false)
                    materialNetworkService.retrieve(config.url).let { materialElement ->
                        materialCacheRepository.refreshCache(adapterConfig, materialElement)
                    }
                }
        }
    }

    override suspend fun retrieve(url: String): MaterialModelDomain {
        return materialCacheRepository.retrieve(DecoderConfig(url = url))
    }
}
