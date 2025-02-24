package com.tezov.tuucho.core.domain.model.material

import com.tezov.tuucho.core.domain.model.material._common.IdModelDomain
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable(with = TextModelDomain.Serializer::class)
class TextModelDomain(
    type: String,
    id: IdModelDomain,
    default: String? = null,
    mapJsonElement: Map<String, JsonElement>? = null,
) : ValueOrObjectModelDomain.Base(type, id, default, mapJsonElement) {

    object Default {
        const val type = "text"
    }

    object Serializer :
        ValueOrObjectModelDomain.Serializer<TextModelDomain>("TextModelDomain") {

        override fun createRef(
            type: String,
            id: IdModelDomain,
            default: String?,
            mapJsonElement: Map<String, JsonElement>?
        ) = TextModelDomain(
            type = type,
            id = id,
            default = default,
            mapJsonElement = mapJsonElement,
        )
    }
}

