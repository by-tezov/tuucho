package com.tezov.tuucho.core.domain.model.material._common.header

import com.tezov.tuucho.core.domain.model._system.mapValueOrObjectNullable
import com.tezov.tuucho.core.domain.model.material.ValueOrObjectModelDomain
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder

interface HeaderMapValueOrObjectModelDomain<T : ValueOrObjectModelDomain> {
    val mapValueOrObject: Map<String, T?>?

    object Name {
        const val mapValueOrObject = "mapValueOrObject"
    }

    object Serializer {

        fun descriptor(descriptorBuilder: ClassSerialDescriptorBuilder) = with(descriptorBuilder) {
            element(
                Name.mapValueOrObject,
                MapSerializer(
                    String.serializer(),
                    ValueOrObjectModelDomain.PolymorphicSerializer
                ).descriptor
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : ValueOrObjectModelDomain, S : KSerializer<T>> JsonObject.mapValueOrObject(
            json: Json,
            serializer: S
        ): Map<String, T?> = (this
                - HeaderTypeModelDomain.Name.type
                - HeaderIdModelDomain.Name.id
                - HeaderHasChildrenModelDomain.Name.hasChildren
                - HeaderSubsetModelDomain.Name.subset
                ).mapValueOrObjectNullable(
                json = json,
                serializer = serializer
            )

        @Suppress("UNCHECKED_CAST")
        fun <T : ValueOrObjectModelDomain, S : KSerializer<T>> JsonObjectBuilder.mapValueOrObject(
            value: Map<String, T?>?,
            json: Json,
            serializer: S
        ) {
            value?.takeIf { it.isNotEmpty() }?.forEach { (key, value) ->
                put(key, value?.let {
                    json.encodeToJsonElement(serializer, it)
                } ?: JsonNull)
            }
        }
    }
}
