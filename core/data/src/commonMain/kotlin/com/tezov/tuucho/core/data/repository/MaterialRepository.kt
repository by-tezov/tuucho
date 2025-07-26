package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.mapping.toExtraDataBreaker
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.data.parser.assembler.ExtraDataAssembler
import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema.material.SettingSchema
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
        val materialElement = materialCacheRepository.retrieve(extraDataAssembler) ?: run {
            val extraDataBreaker = ExtraDataBreaker(url = url, version = "", isShared = false)
            materialNetworkService.retrieve(url).let { materialElement ->
                materialCacheRepository.refreshCache(extraDataBreaker, materialElement)
                materialCacheRepository.retrieve(extraDataAssembler) ?: TODO("retrieve url $url returned nothing")
            }
        }
        materialElement.onScope(SettingSchema::Scope)
            .takeIf { it.missingDefinition == true }
            ?.let {
                println("need to download missing data")

                //TODO :
                // - retrieve all the onDemand definition, it must be an where all setting are merged on root
                // - retreive all json rectify, break, assemble -> if can be done without recording them
                // (or idea, recorded in temp table, element retrieve could have ttl too to save some bandwich)
                // - then assemble with the missing definition retrieved
                // // -> or let the render be done with "schimmer", then do this job on back thread and then update the view when ready
                // with missing content

            }
        return materialElement
    }

    override suspend fun send(url: String, data: JsonElement): JsonElement? {
        return materialNetworkService.send(url, data)
    }
}
