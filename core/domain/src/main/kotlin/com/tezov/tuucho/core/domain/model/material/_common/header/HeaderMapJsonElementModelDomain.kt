package com.tezov.tuucho.core.domain.model.material._common.header

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder

interface HeaderMapJsonElementModelDomain {
    val mapJsonElement: Map<String, JsonElement>?

    object Name {
        const val mapJsonElement = "mapJsonElement"
    }

    object Serializer {

        fun descriptor(descriptorBuilder: ClassSerialDescriptorBuilder) = with(descriptorBuilder) {
            element(
                Name.mapJsonElement,
                MapSerializer(String.serializer(), JsonElement.serializer()).descriptor
            )
        }

        fun JsonObject.mapJsonElement() = (this
                - HeaderTypeModelDomain.Name.type
                - HeaderIdModelDomain.Name.id
                - HeaderHasChildrenModelDomain.Name.hasChildren
                - HeaderSubsetModelDomain.Name.subset
                )

        fun JsonObjectBuilder.mapJsonElement(
            value: Map<String, JsonElement>?
        ) {
            value?.takeIf { it.isNotEmpty() }?.forEach { (key, value) ->
                put(key, value)
            }
        }
    }
}



