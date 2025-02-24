package com.tezov.tuucho.core.domain.model.material._common.header

import com.tezov.tuucho.core.domain.model._system.booleanOrNull
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive

interface HeaderHasChildrenModelDomain {
    val hasChildren: Boolean

    object Name {
        const val hasChildren = "hasChildren"
    }

    object Default {
        val hasChildren: Boolean = false
    }

    object Serializer {

        fun descriptor(descriptorBuilder: ClassSerialDescriptorBuilder) = with(descriptorBuilder) {
            element(Name.hasChildren, Boolean.serializer().descriptor)
        }

        fun JsonObject.hasChildren() =
            this[Name.hasChildren].booleanOrNull ?: Default.hasChildren

        fun JsonObjectBuilder.hasChildren(
            value: Boolean
        ) {
            put(Name.hasChildren, JsonPrimitive(value))
        }
    }

}



