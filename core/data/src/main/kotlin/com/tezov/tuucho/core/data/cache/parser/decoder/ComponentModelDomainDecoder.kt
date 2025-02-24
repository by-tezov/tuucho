package com.tezov.tuucho.core.data.cache.parser.decoder

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.cache.parser.decoder._system.merge
import com.tezov.tuucho.core.data.cache.parser.decoder._system.resolveRefs
import com.tezov.tuucho.core.domain.model.material.ComponentModelDomain
import com.tezov.tuucho.core.domain.model.material.DefaultComponentModelDomain
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ComponentModelDomainDecoder(
    private val database: Database
) : JsonEntityDecoderToModelDomain<JsonElement>, KoinComponent {

    private val optionModelDomainDecoder: OptionModelDomainDecoder by inject()
    private val styleModelDomainDecoder: StyleModelDomainDecoder by inject()
    private val contentModelDomainDecoder: ContentModelDomainDecoder by inject()

    override suspend fun decode(
        element: JsonElement,
        config: DecoderConfig
    ) = element
        .resolveRefs(config.url, ComponentModelDomain.Default.type, database)
        .map { decodeParts(it, config) }
        .merge()

    private suspend fun decodeParts(
        value: JsonElement,
        config: DecoderConfig
    ): JsonElement {
        val style = value.jsonObject[DefaultComponentModelDomain.Name.style]?.let { content ->
            styleModelDomainDecoder.decode(content, config)
        }
        val option = value.jsonObject[DefaultComponentModelDomain.Name.option]?.let { content ->
            optionModelDomainDecoder.decode(content, config)
        }
        val content = value.jsonObject[DefaultComponentModelDomain.Name.content]?.let { content ->
            contentModelDomainDecoder.decode(content, config)
        }
        return value.replace(style, option, content)
    }

    fun JsonElement.replace(
        style:JsonElement?,
        option:JsonElement?,
        content:JsonElement?,
    ): JsonElement {
        val map = this.jsonObject.toMutableMap()
        style?.let {  map[DefaultComponentModelDomain.Name.style] = it }
        option?.let {  map[DefaultComponentModelDomain.Name.option] = it }
        content?.let {  map[DefaultComponentModelDomain.Name.content] = it }
        return JsonObject(map)
    }

}