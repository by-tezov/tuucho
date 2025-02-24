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

@Serializable(with = OptionModelDomain.PolymorphicSerializer::class)
interface OptionModelDomain : HeaderTypeModelDomain, HeaderIdModelDomain, HeaderSubsetModelDomain {

    object Default {
        const val type = "option"
    }

    object PolymorphicSerializer :
        JsonContentPolymorphicSerializer<OptionModelDomain>(OptionModelDomain::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<OptionModelDomain> {
            with(element.jsonObject) {
                if (this.isRefModelDomain()) {
                    return RefOptionModelDomain.serializer()
                }
                return GenericOptionModelDomain.serializer()
            }
        }
    }
}

@Serializable(with = RefOptionModelDomain.Serializer::class)
class RefOptionModelDomain(
    type: String,
    id: IdModelDomain
) : RefModelDomain(type, id), OptionModelDomain {

    override val subset = null

    object Serializer : RefModelDomain.Serializer<RefOptionModelDomain>("RefOptionModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain
        ) = RefOptionModelDomain(
            type = type,
            id = id
        )
    }
}

@Serializable(with = GenericOptionModelDomain.Serializer::class)
class GenericOptionModelDomain(
    override val type: String,
    override val id: IdModelDomain,
    subset: ComponentModelDomainSubset? = null,
    mapJsonElement: Map<String, JsonElement>? = null,
) : GenericModelDomain(type, id, subset, mapJsonElement), OptionModelDomain {

    object Serializer :
        GenericModelDomain.Serializer<GenericOptionModelDomain>("GenericOptionModelDomain") {
        override fun createRef(
            type: String,
            id: IdModelDomain,
            subset: ComponentModelDomainSubset?,
            mapJsonElement: Map<String, JsonElement>?
        ) = GenericOptionModelDomain(
            type,
            id,
            subset,
            mapJsonElement
        )
    }
}
