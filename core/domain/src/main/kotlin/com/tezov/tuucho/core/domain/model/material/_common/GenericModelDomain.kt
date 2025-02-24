package com.tezov.tuucho.core.domain.model.material._common

import com.tezov.tuucho.core.domain.model._system.jsonDecoder
import com.tezov.tuucho.core.domain.model._system.jsonEncoder
import com.tezov.tuucho.core.domain.model._system.jsonObject
import com.tezov.tuucho.core.domain.model.material.ComponentModelDomainSubset
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain.Serializer.id
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderMapJsonElementModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderMapJsonElementModelDomain.Serializer.mapJsonElement
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain.Serializer.subset
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain.Serializer.subsetOrNull
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain.Serializer.type
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject

abstract class GenericModelDomain(
    override val type: String,
    override val id: IdModelDomain,
    override val subset: ComponentModelDomainSubset? = null,
    override val mapJsonElement: Map<String, JsonElement>? = null
) : HeaderTypeModelDomain, HeaderIdModelDomain, HeaderSubsetModelDomain, HeaderMapJsonElementModelDomain {

    override fun toString(): String {
        return StringBuilder().apply {
            append("${this@GenericModelDomain::class.simpleName}(")
            append("type=${type},")
            append("id=${id},")
            append("subset=${subset},")
            append("mapJsonElement=${mapJsonElement}")
            append(")")
        }.toString()
    }

    abstract class Serializer<T : GenericModelDomain>(
        val name: String
    ) : KSerializer<T> {

        abstract fun createRef(
            type: String,
            id: IdModelDomain,
            subset: ComponentModelDomainSubset?,
            mapJsonElement: Map<String, JsonElement>?
        ): T

        override val descriptor
            get() = buildClassSerialDescriptor(name) {
                HeaderTypeModelDomain.Serializer.descriptor(this)
                HeaderIdModelDomain.Serializer.descriptor(this)
                HeaderSubsetModelDomain.Serializer.descriptor(this)
                HeaderMapJsonElementModelDomain.Serializer.descriptor(this)
            }

        override fun deserialize(decoder: Decoder): T {
            val jsonDecoder = decoder.jsonDecoder
            with(decoder.jsonObject) {
                return createRef(type(), id(jsonDecoder.json), subsetOrNull(), mapJsonElement())
            }
        }

        override fun serialize(encoder: Encoder, value: T) {
            val jsonEncoder = encoder.jsonEncoder
            buildJsonObject {
                type(value.type)
                id(jsonEncoder.json, value.id)
                subset(value.subset)
                mapJsonElement(value.mapJsonElement)
            }.also(encoder.jsonEncoder::encodeJsonElement)
        }
    }
}
