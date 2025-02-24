//package com.tezov.tuucho.core.data.cache.parser.decoder.label
//
//import com.tezov.tuucho.core.data.cache.parser.decoder.DecoderConfig
//import com.tezov.tuucho.core.data.cache.parser.decoder.JsonEntityDecoderToModelDomain
//import com.tezov.tuucho.core.data.cache.parser.decoder.TextModelDomainDecoder
//import com.tezov.tuucho.core.domain.model.material.label.LabelContentModelDomain
//import kotlinx.serialization.json.JsonElement
//import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.jsonObject
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//
//class LabelContentModelDomainDecoder : JsonEntityDecoderToModelDomain<JsonElement>, KoinComponent {
//
//    private val textModelDomainDecoder: TextModelDomainDecoder by inject()
//
//    override suspend fun decode(
//        element: JsonElement,
//        config: DecoderConfig
//    ): JsonElement {
//        val value = element.jsonObject[LabelContentModelDomain.Name.value]?.let {
//            textModelDomainDecoder.decode(it, config)
//        }
//        return value?.let { element.replace(it) } ?: element
//    }
//
//    private fun JsonElement.replace(
//        value: JsonElement,
//    ): JsonElement {
//        val map = this.jsonObject.toMutableMap()
//        map[LabelContentModelDomain.Name.value] = value
//        return JsonObject(map)
//    }
//
//}