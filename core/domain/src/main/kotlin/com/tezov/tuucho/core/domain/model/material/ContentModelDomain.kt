package com.tezov.tuucho.core.domain.model.material

import com.tezov.tuucho.core.domain.model._system.isRefModelDomain
import com.tezov.tuucho.core.domain.model.material._common.GenericModelDomain
import com.tezov.tuucho.core.domain.model.material._common.IdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.RefModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderHasChildrenModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = ContentModelDomain.PolymorphicSerializer::class)
interface ContentModelDomain : HeaderTypeModelDomain, HeaderIdModelDomain,
    HeaderHasChildrenModelDomain, HeaderSubsetModelDomain {

    object Default {
        const val type = "content"
    }

    object PolymorphicSerializer :
        JsonContentPolymorphicSerializer<ContentModelDomain>(ContentModelDomain::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ContentModelDomain> {
            with(element.jsonObject) {
                if (this.isRefModelDomain()) {
                    return RefContentModelDomain.serializer()
                }
                return GenericContentModelDomain.serializer()
            }
        }
    }
}

@Serializable(with = RefContentModelDomain.Serializer::class)
class RefContentModelDomain(
    type: String,
    id: IdModelDomain
) : RefModelDomain(type, id), ContentModelDomain {

    override val subset = null

    override val hasChildren = false

    object Serializer : RefModelDomain.Serializer<RefContentModelDomain>("RefContentModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain
        ) = RefContentModelDomain(
            type = type,
            id = id
        )
    }
}

@Serializable(with = GenericContentModelDomain.Serializer::class)
class GenericContentModelDomain(
    override val type: String,
    override val id: IdModelDomain,
    subset: ComponentModelDomainSubset? = null,
    mapJsonElement: Map<String, JsonElement>? = null,
) : GenericModelDomain(type, id, subset, mapJsonElement), ContentModelDomain {

    override val hasChildren = false

    object Serializer :
        GenericModelDomain.Serializer<GenericContentModelDomain>("GenericContentModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain,
            subset: ComponentModelDomainSubset?,
            mapJsonElement: Map<String, JsonElement>?
        ) = GenericContentModelDomain(
            type,
            id,
            subset,
            mapJsonElement
        )
    }


}


