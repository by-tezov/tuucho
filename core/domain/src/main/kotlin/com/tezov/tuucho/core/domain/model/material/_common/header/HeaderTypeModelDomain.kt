package com.tezov.tuucho.core.domain.model.material._common.header

import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive

interface HeaderTypeModelDomain {
    val type: String

    object Name {
        const val type = "type"
    }

    object Serializer {

        fun descriptor(descriptorBuilder: ClassSerialDescriptorBuilder) = with(descriptorBuilder) {
            element(Name.type, String.serializer().descriptor)
        }

        fun JsonObject.type() = this[Name.type].string
        fun JsonObject.typeOrNull() = this[Name.type].stringOrNull

        fun JsonObjectBuilder.type(
            value: String
        ) {
            put(Name.type, JsonPrimitive(value))
        }
    }

}



