//package com.tezov.tuucho.core.data.cache.parser.decoder.layout.linear
//
//import com.tezov.tuucho.core.data.cache.parser.decoder.ComponentModelDomainDecoder
//import com.tezov.tuucho.core.data.cache.parser.decoder.DecoderConfig
//import com.tezov.tuucho.core.data.cache.parser.decoder.JsonEntityDecoderToModelDomain
//import com.tezov.tuucho.core.domain.model.material.layout.linear.LayoutLinearContentModelDomain
//import kotlinx.serialization.json.JsonArray
//import kotlinx.serialization.json.JsonElement
//import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.jsonArray
//import kotlinx.serialization.json.jsonObject
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//
//class LayoutLinearContentModelDomainDecoder : JsonEntityDecoderToModelDomain<JsonElement>,
//    KoinComponent {
//
//    private val componentModelDomainDecoder: ComponentModelDomainDecoder by inject()
//
//    override suspend fun decode(
//        element: JsonElement,
//        config: DecoderConfig
//    ): JsonElement {
//        val items = element.jsonObject[LayoutLinearContentModelDomain.Name.items]?.let { items ->
//            JsonArray(items.jsonArray.map { item ->
//                componentModelDomainDecoder.decode(item, config)
//            })
//        }
//        return items?.let { element.replace(it) } ?: element
//    }
//
//    private fun JsonElement.replace(
//        items:JsonArray,
//    ): JsonElement {
//        val map = this.jsonObject.toMutableMap()
//        map[LayoutLinearContentModelDomain.Name.items] = items
//        return JsonObject(map)
//    }
//}