package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.mapping.toExtraDataBreaker
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.data.parser.assembler.ExtraDataAssembler
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class MaterialRepositoryImpl(
    private val materialNetworkService: MaterialNetworkService,
    private val materialCacheRepository: MaterialCacheRepository,
) : MaterialRepositoryProtocol {

    override suspend fun refreshCache(url: String) {
        val configModelDomain = materialNetworkService.retrieveConfig(url)
        configModelDomain.preload.apply {
            subs
                .filter { materialCacheRepository.shouldRefresh(it.url, it.version) }
                .forEach { config ->
                    val extraData = config.toExtraDataBreaker(true)
                    materialNetworkService.retrieve(config.url).let { materialElement ->
                        materialCacheRepository.refreshCache(extraData, materialElement)
                    }
                }
            templates
                .filter { materialCacheRepository.shouldRefresh(it.url, it.version) }
                .forEach { config ->
                    val extraData = config.toExtraDataBreaker(true)
                    materialNetworkService.retrieve(config.url).let { materialElement ->
                        materialCacheRepository.refreshCache(extraData, materialElement)
                    }
                }
            pages
                .filter { materialCacheRepository.shouldRefresh(it.url, it.version) }
                .forEach { config ->
                    val extraData = config.toExtraDataBreaker(false)
                    materialNetworkService.retrieve(config.url).let { materialElement ->
                        materialCacheRepository.refreshCache(extraData, materialElement)
                    }
                }
        }
    }

    override suspend fun retrieve(url: String): JsonObject {
        val extraData = ExtraDataAssembler(url = url)
        return materialCacheRepository.retrieve(extraData)
    }
}
