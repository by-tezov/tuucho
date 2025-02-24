package com.tezov.tuucho.core.domain.model.material

import com.tezov.tuucho.core.domain.model._system.isRefModelDomain
import com.tezov.tuucho.core.domain.model.material._common.GenericModelDomain
import com.tezov.tuucho.core.domain.model.material._common.IdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.RefModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = StyleModelDomain.PolymorphicSerializer::class)
interface StyleModelDomain : HeaderTypeModelDomain, HeaderIdModelDomain, HeaderSubsetModelDomain {

    object Default {
        const val type = "style"
    }

    object PolymorphicSerializer :
        JsonContentPolymorphicSerializer<StyleModelDomain>(StyleModelDomain::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<StyleModelDomain> {
            with(element.jsonObject) {
                if (this.isRefModelDomain()) {
                    return RefStyleModelDomain.serializer()
                }
                return GenericStyleModelDomain.serializer()
            }
        }
    }
}

@Serializable(with = RefStyleModelDomain.Serializer::class)
class RefStyleModelDomain(
    type: String,
    id: IdModelDomain
) : RefModelDomain(type, id), StyleModelDomain {

    override val subset = null

    object Serializer : RefModelDomain.Serializer<RefStyleModelDomain>("RefStyleModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain
        ) = RefStyleModelDomain(
            type = type,
            id = id
        )
    }
}

@Serializable(with = GenericStyleModelDomain.Serializer::class)
class GenericStyleModelDomain(
    override val type: String,
    override val id: IdModelDomain,
    subset: ComponentModelDomainSubset? = null,
    mapJsonElement: Map<String, JsonElement>? = null,
) : GenericModelDomain(type, id, subset, mapJsonElement), StyleModelDomain {

    object Serializer :
        GenericModelDomain.Serializer<GenericStyleModelDomain>("GenericStyleModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain,
            subset: ComponentModelDomainSubset?,
            mapJsonElement: Map<String, JsonElement>?
        ) = GenericStyleModelDomain(
            type,
            id,
            subset,
            mapJsonElement
        )
    }
}

