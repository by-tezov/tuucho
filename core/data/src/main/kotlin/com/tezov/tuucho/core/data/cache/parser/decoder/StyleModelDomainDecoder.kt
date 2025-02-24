package com.tezov.tuucho.core.data.cache.parser.decoder

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.cache.parser.decoder._system.merge
import com.tezov.tuucho.core.data.cache.parser.decoder._system.resolveRefs
import com.tezov.tuucho.core.data.di.MaterialDecoderModule.Name
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import com.tezov.tuucho.core.domain.model.material.StyleModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StyleModelDomainDecoder(
    private val database: Database,
) : JsonEntityDecoderToModelDomain<JsonElement>, KoinComponent {

    private val decoders: Map<String, JsonEntityDecoderToModelDomain<JsonElement>> by inject(
        Name.Decoder.STYLE_MAP
    )

    override suspend fun decode(
        element: JsonElement,
        config: DecoderConfig
    ) = element
    .resolveRefs(config.url, StyleModelDomain.Default.type, database)
    .map {
        decoders[it.jsonObject[HeaderSubsetModelDomain.Name.subset].stringOrNull]
            ?.decode(it, config) ?: it
    }
    .merge()
}