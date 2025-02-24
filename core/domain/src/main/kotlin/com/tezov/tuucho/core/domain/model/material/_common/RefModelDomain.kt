package com.tezov.tuucho.core.domain.model.material._common

import com.tezov.tuucho.core.domain.model._system.jsonDecoder
import com.tezov.tuucho.core.domain.model._system.jsonEncoder
import com.tezov.tuucho.core.domain.model._system.jsonObject
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain.Serializer.id
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain.Serializer.type
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.buildJsonObject

abstract class RefModelDomain(
    override val type: String,
    override val id: IdModelDomain,
) : HeaderTypeModelDomain, HeaderIdModelDomain {

    override fun toString(): String {
        return StringBuilder().apply {
            append("${this@RefModelDomain::class.simpleName}(")
            append("type=${type},")
            append("id=${id},")
            append(")")
        }.toString()
    }

    abstract class Serializer<T : RefModelDomain>(
        val name: String
    ) : KSerializer<T> {

        abstract fun createRef(type: String, id: IdModelDomain): T

        override val descriptor
            get() = buildClassSerialDescriptor(name) {
                HeaderTypeModelDomain.Serializer.descriptor(this)
                HeaderIdModelDomain.Serializer.descriptor(this)
                HeaderSubsetModelDomain.Serializer.descriptor(this)
            }

        override fun deserialize(decoder: Decoder): T {
            val jsonDecoder = decoder.jsonDecoder
            with(decoder.jsonObject) {
                return createRef(type(), id(jsonDecoder.json))
            }
        }

        override fun serialize(encoder: Encoder, value: T) {
            val jsonEncoder = encoder.jsonEncoder
            buildJsonObject {
                type(value.type)
                id(jsonEncoder.json, value.id)
            }.also(encoder.jsonEncoder::encodeJsonElement)
        }
    }
}
