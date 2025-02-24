package com.tezov.tuucho.core.domain.model.material._common.header

import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import com.tezov.tuucho.core.domain.model.material.ComponentModelDomainSubset
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain.Name.subset
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive

interface HeaderSubsetModelDomain {
    val subset: ComponentModelDomainSubset?

    object Name {
        const val subset = "subset"
    }

    object Serializer {

        fun descriptor(descriptorBuilder: ClassSerialDescriptorBuilder) = with(descriptorBuilder) {
            element(subset, String.serializer().descriptor.nullable)
        }

        fun JsonObject.subset() = this[subset].string.let {
            ComponentModelDomainSubset.from(it)
        }
        fun JsonObject.subsetOrNull() = this[subset].stringOrNull?.let {
            ComponentModelDomainSubset.fromOrNull(it)
        }

        fun JsonObjectBuilder.subset(
            value: ComponentModelDomainSubset?
        ) {
            value?.let { put(subset, JsonPrimitive(it.value)) }
        }
    }

}



