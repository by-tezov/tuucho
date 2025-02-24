//package com.tezov.tuucho.core.data.cache.parser.encoder.layout.linear
//
//import com.tezov.tuucho.core.data.cache.entity.JsonObjectEntity
//import com.tezov.tuucho.core.data.parser.encoder.ComponentModelDomainEncoder
//import com.tezov.tuucho.core.data.parser.encoder.EncoderConfig
//import com.tezov.tuucho.core.data.cache.parser.encoder.ModelDomainEncoderToJsonEntity
//import com.tezov.tuucho.core.data.cache.parser.encoder._system.shouldBeEncoded
//import com.tezov.tuucho.core.domain.model.material.ComponentModelDomain
//import com.tezov.tuucho.core.domain.model.material.RefComponentModelDomain
//import com.tezov.tuucho.core.domain.model.material._common.header.HeaderIdsModelDomain
//import com.tezov.tuucho.core.domain.model.material.layout.linear.LayoutLinearContentModelDomain
//import kotlinx.serialization.json.Json
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//
//class LayoutLinearContentModelDomainEncoder(
//    private val json: Json
//) : ModelDomainEncoderToJsonEntity<LayoutLinearContentModelDomain>, KoinComponent {
//
//    private val componentModelDomainEncoder: ComponentModelDomainEncoder by inject()
//
//    override fun encode(model: LayoutLinearContentModelDomain, config: EncoderConfig) =  with(model) {
//            val children = mutableListOf<JsonObjectEntity>()
//            val valueAltered = LayoutLinearContentModelDomain(
//                type = type,
//                id = id,
//                subset = subset,
//                items = items?.mapNotNull { children.addEncoded(it, config) }
//            )
//
//            JsonObjectEntity(
//                url = config.url,
//                id = id.value,
//                idFrom = id.source,
//                type = type,
//                jsonElement = json.encodeToJsonElement(
//                    LayoutLinearContentModelDomain.serializer(),
//                    valueAltered
//                )
//            ).apply {
//                this.children = children
//            }
//        }
//
//    private fun MutableList<JsonObjectEntity>.addEncoded(
//        value: ComponentModelDomain?,
//        config: EncoderConfig
//    ) = value?.let {
//        if (value.shouldBeEncoded()) {
//            val encoded = componentModelDomainEncoder.encode(value, config).also(this::add)
//            RefComponentModelDomain(type = encoded.type, id = HeaderIdsModelDomain.Id(value = encoded.id))
//        } else {
//            // check the todo inside domain.IdReference
//            value
//        }
//    }
//}
//
//
