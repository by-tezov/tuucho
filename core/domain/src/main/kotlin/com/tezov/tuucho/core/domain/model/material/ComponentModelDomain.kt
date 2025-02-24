package com.tezov.tuucho.core.domain.model.material

import com.tezov.tuucho.core.domain.model._system.isRefModelDomain
import com.tezov.tuucho.core.domain.model._system.jsonDecoder
import com.tezov.tuucho.core.domain.model._system.jsonEncoder
import com.tezov.tuucho.core.domain.model._system.jsonObject
import com.tezov.tuucho.core.domain.model.material._common.IdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.RefModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderHasChildrenModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderHasChildrenModelDomain.Serializer.hasChildren
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain.Serializer.id
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain.Serializer.subset
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain.Serializer.subsetOrNull
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain.Serializer.type
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

@Serializable(with = ComponentModelDomain.PolymorphicSerializer::class)
interface ComponentModelDomain : HeaderTypeModelDomain, HeaderIdModelDomain,
    HeaderHasChildrenModelDomain,
    HeaderSubsetModelDomain {

    object Default {
        const val type = "component"
    }

    object PolymorphicSerializer : JsonContentPolymorphicSerializer<ComponentModelDomain>(
        ComponentModelDomain::class
    ) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ComponentModelDomain> {
            if (element.jsonObject.isRefModelDomain()) {
                return RefComponentModelDomain.serializer()
            }
            return DefaultComponentModelDomain.serializer()
        }
    }
}

@Serializable(with = RefComponentModelDomain.Serializer::class)
class RefComponentModelDomain(
    type: String,
    id: IdModelDomain
) : RefModelDomain(type, id), ComponentModelDomain {

    override val subset = null

    override val hasChildren = false

    object Serializer :
        RefModelDomain.Serializer<RefComponentModelDomain>("RefComponentModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain
        ) = RefComponentModelDomain(
            type = type,
            id = id
        )
    }
}

@Serializable(with = DefaultComponentModelDomain.Serializer::class)
data class DefaultComponentModelDomain(
    override val type: String = ComponentModelDomain.Default.type,
    override val id: IdModelDomain,
    override val subset: ComponentModelDomainSubset? = null,
    val option: OptionModelDomain? = null,
    val style: StyleModelDomain? = null,
    val content: ContentModelDomain? = null
) : ComponentModelDomain {

    override val hasChildren = true

    object Name {
        const val option = "option"
        const val style = "style"
        const val content = "content"
    }

    internal object Serializer : KSerializer<DefaultComponentModelDomain> {

        override val descriptor
            get() = buildClassSerialDescriptor("DefaultComponentModelDomain") {
                HeaderTypeModelDomain.Serializer.descriptor(this)
                HeaderIdModelDomain.Serializer.descriptor(this)
                HeaderSubsetModelDomain.Serializer.descriptor(this)
                element(Name.option, OptionModelDomain.PolymorphicSerializer.descriptor.nullable)
                element(Name.style, StyleModelDomain.PolymorphicSerializer.descriptor.nullable)
                element(Name.content, ContentModelDomain.PolymorphicSerializer.descriptor.nullable)
            }

        override fun deserialize(decoder: Decoder): DefaultComponentModelDomain {
            val jsonDecoder = decoder.jsonDecoder
            with(decoder.jsonObject) {
                val subset = subsetOrNull()
                val option = this[Name.option]?.jsonObject?.let { option ->
                    jsonDecoder.json.decodeFromJsonElement(
                        OptionModelDomain.PolymorphicSerializer,
                        option
                    )
                }
                val style = this[Name.style]?.jsonObject?.let { style ->
                    jsonDecoder.json.decodeFromJsonElement(
                        StyleModelDomain.PolymorphicSerializer,
                        style
                    )
                }
                val content = this[Name.content]?.jsonObject?.let { content ->
                    jsonDecoder.json.decodeFromJsonElement(
                        ContentModelDomain.PolymorphicSerializer,
                        content
                    )
                }
                return DefaultComponentModelDomain(
                    type = type(),
                    id = id(jsonDecoder.json),
                    subset = subset,
                    option = option,
                    style = style,
                    content = content
                )
            }
        }

        override fun serialize(encoder: Encoder, value: DefaultComponentModelDomain) {
            val jsonEncoder = encoder.jsonEncoder
            buildJsonObject {
                type(value.type)
                id(jsonEncoder.json, value.id)
                hasChildren(value.hasChildren)
                subset(value.subset)
                value.option?.let {
                    put(
                        Name.option,
                        jsonEncoder.json.encodeToJsonElement(
                            OptionModelDomain.PolymorphicSerializer,
                            it
                        )
                    )
                }
                value.style?.let {
                    put(
                        Name.style,
                        jsonEncoder.json.encodeToJsonElement(
                            StyleModelDomain.PolymorphicSerializer,
                            it
                        )
                    )
                }
                value.content?.let {
                    put(
                        Name.content,
                        jsonEncoder.json.encodeToJsonElement(
                            ContentModelDomain.PolymorphicSerializer,
                            it
                        )
                    )
                }
            }.also(jsonEncoder::encodeJsonElement)
        }
    }

}



