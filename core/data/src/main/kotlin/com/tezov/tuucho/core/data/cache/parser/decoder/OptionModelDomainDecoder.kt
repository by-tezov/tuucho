package com.tezov.tuucho.core.data.cache.parser.decoder

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.cache.parser.decoder._system.merge
import com.tezov.tuucho.core.data.cache.parser.decoder._system.resolveRefs
import com.tezov.tuucho.core.domain.model.material.OptionModelDomain
import kotlinx.serialization.json.JsonElement

class OptionModelDomainDecoder(
    private val database: Database,
) : JsonEntityDecoderToModelDomain<JsonElement> {

    override suspend fun decode(
        element: JsonElement,
        config: DecoderConfig
    ) = element
        .resolveRefs(config.url, OptionModelDomain.Default.type, database)
        .merge()
}