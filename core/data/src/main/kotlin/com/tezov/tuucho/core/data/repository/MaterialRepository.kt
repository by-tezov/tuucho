package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.mapping.toExtraDataBreaker
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.data.parser.assembler.ExtraDataAssembler
import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonElement

class MaterialRepository(
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

    override suspend fun retrieve(url: String): JsonElement {
        val extraDataAssembler = ExtraDataAssembler(url = url)
        return materialCacheRepository.retrieve(extraDataAssembler) ?: run {
            val extraDataBreaker = ExtraDataBreaker(url, version = "", false)
            materialNetworkService.retrieve(url).let { materialElement ->
                materialCacheRepository.refreshCache(extraDataBreaker, materialElement)
                materialCacheRepository.retrieve(extraDataAssembler) ?: TODO()
            }
        }
    }

    override suspend fun send(url: String, data: JsonElement): JsonElement? {
        return materialNetworkService.send(url, data)
    }
}
