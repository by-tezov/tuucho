package com.tezov.tuucho.core.domain.model.material

import android.util.MalformedJsonException
import com.tezov.tuucho.core.domain.model._system.jsonDecoder
import com.tezov.tuucho.core.domain.model._system.jsonEncoder
import com.tezov.tuucho.core.domain.model._system.jsonObject
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import com.tezov.tuucho.core.domain.model.material._common.IdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain.Serializer.id
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderMapJsonElementModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderMapJsonElementModelDomain.Serializer.mapJsonElement
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain.Serializer.type
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

@Serializable(with = ValueOrObjectModelDomain.PolymorphicSerializer::class)
interface ValueOrObjectModelDomain : HeaderTypeModelDomain, HeaderIdModelDomain,
    HeaderMapJsonElementModelDomain {

    object Name {
        const val default = "default"
    }

    object PolymorphicSerializer :
        JsonContentPolymorphicSerializer<ValueOrObjectModelDomain>(ValueOrObjectModelDomain::class) {

            override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ValueOrObjectModelDomain> {
            with(element.jsonObject) {
                return when (type()) {
                    ColorModelDomain.Default.type -> ColorModelDomain.serializer()
                    DimensionModelDomain.Default.type -> DimensionModelDomain.serializer()
                    TextModelDomain.Default.type -> TextModelDomain.serializer()
                    else -> throw MalformedJsonException("type not managed")
                }
            }
        }
    }

    abstract class Serializer<T : Base>(
        val name: String
    ) : KSerializer<T> {

        abstract fun createRef(
            type: String,
            id: IdModelDomain,
            default: String? = null,
            mapJsonElement: Map<String, JsonElement>? = null
        ): T

        override val descriptor
            get() = buildClassSerialDescriptor(name) {
                HeaderTypeModelDomain.Serializer.descriptor(this)
                HeaderIdModelDomain.Serializer.descriptor(this)
                element(Name.default, String.serializer().descriptor.nullable)
                HeaderMapJsonElementModelDomain.Serializer.descriptor(this)
            }

        override fun deserialize(decoder: Decoder): T {
            val jsonDecoder = decoder.jsonDecoder
            with(decoder.jsonObject) {
                val mapJsonElement = mapJsonElement()
                val default = this[Name.default].stringOrNull
                    ?: mapJsonElement[Name.default].stringOrNull
                return createRef(
                    type = type(),
                    id = id(jsonDecoder.json),
                    default = default,
                    mapJsonElement = mapJsonElement.minus(
                        Name.default
                    )
                )
            }
        }

        override fun serialize(encoder: Encoder, value: T) {
            val jsonEncoder = encoder.jsonEncoder
            buildJsonObject {
                type(value.type)
                id(jsonEncoder.json, value.id)
                put(Name.default, JsonPrimitive(value.default))
                mapJsonElement(value.mapJsonElement)
            }.also(encoder.jsonEncoder::encodeJsonElement)
        }
    }

    abstract class Base(
        override val type: String,
        override val  id: IdModelDomain,
        val default: String? = null,
        override val mapJsonElement: Map<String, JsonElement>? = null,
    ) : ValueOrObjectModelDomain {

        override fun toString(): String {
            return StringBuilder().apply {
                append("${this@Base::class.simpleName}(")
                append("type=${type},")
                append("id=${id},")
                append("default=${default},")
                append("mapJsonElement=${mapJsonElement}")
                append(")")
            }.toString()
        }
    }
}



