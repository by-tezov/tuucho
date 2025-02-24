package com.tezov.tuucho.core.domain.model.material

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ComponentModelDomainSubset.Serializer::class)
enum class ComponentModelDomainSubset(
    val value: String
) {
    LayoutLinear("layout-linear"),
    Label("label"),
    Button("button");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromOrNull(value: String) =
            ComponentModelDomainSubset.entries.firstOrNull { value == it.value }

        fun from(value: String) =
            ComponentModelDomainSubset.entries.first { value == it.value }
    }

    internal object Serializer : KSerializer<ComponentModelDomainSubset> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ComponentModelDomainSubset", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ComponentModelDomainSubset) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): ComponentModelDomainSubset {
            val value = decoder.decodeString()
            return from(value)
        }
    }
}