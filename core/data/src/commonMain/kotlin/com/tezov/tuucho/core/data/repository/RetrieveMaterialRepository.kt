package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.MaterialCacheSource
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.assembler._system.ArgumentAssembler
import com.tezov.tuucho.core.data.parser.breaker._system.ArgumentBreaker
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.SettingSchema
import com.tezov.tuucho.core.domain.model.schema.setting.ConfigSchema
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonElement

class RetrieveMaterialRepository(
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialCacheSource: MaterialCacheSource,
) : RetrieveMaterialRepositoryProtocol {

    override suspend fun retrieve(url: String): JsonElement {
        val argumentAssembler = ArgumentAssembler(url = url)
        val materialElement = materialCacheSource.retrieve(
            argumentAssembler
        ) ?: run {
            materialNetworkSource.retrieve(url).let { materialElement ->
                materialCacheSource.refreshCache(
                    version = materialElement.version(),
                    argumentAssembler = argumentAssembler,
                    argumentBreaker = ArgumentBreaker(url = url),
                    materialElement = materialElement
                )
                materialCacheSource.retrieve(argumentAssembler)
                    ?: TODO("retrieve url $url returned nothing")
            }
        }

        materialElement.onScope(SettingSchema::Scope)
            .takeIf { it.missingDefinition == true }
            ?.let {
                //println("need to download missing data")

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

    private fun JsonElement.version() = withScope(ConfigSchema.MaterialItem::Scope).version
        ?: throw DataException.Default("missing version in page material $this")

}
