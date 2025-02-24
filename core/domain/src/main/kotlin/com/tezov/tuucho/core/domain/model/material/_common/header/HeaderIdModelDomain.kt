package com.tezov.tuucho.core.domain.model.material._common.header

import com.tezov.tuucho.core.domain.model.material._common.IdModelDomain
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder

interface HeaderIdModelDomain {

    val id: IdModelDomain

    object Name {
        const val id = "id"
    }

    object Serializer {

        fun descriptor(descriptorBuilder: ClassSerialDescriptorBuilder) = with(descriptorBuilder) {
            element(Name.id, IdModelDomain.serializer().descriptor.nullable)
        }

        fun JsonObject.id(json: Json): IdModelDomain = json.decodeFromJsonElement(IdModelDomain.serializer(), this[Name.id]!!)
        fun JsonObjectBuilder.id(
            json: Json,
            value: IdModelDomain?
        ) {
            value?.let{
                put(Name.id, json.encodeToJsonElement(IdModelDomain.serializer(), it))
            }
        }
    }

}



