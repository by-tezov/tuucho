//package com.tezov.tuucho.core.data.cache.parser.decoder.label
//
//import com.tezov.tuucho.core.data.cache.database.Database
//import com.tezov.tuucho.core.data.cache.parser.decoder.DecoderConfig
//import com.tezov.tuucho.core.data.cache.parser.decoder.JsonEntityDecoderToModelDomain
//import kotlinx.serialization.json.JsonElement
//
//class LabelStyleModelDomainDecoder(
//    private val database: Database,
//) : JsonEntityDecoderToModelDomain<JsonElement> {
//
//    override suspend fun decode(
//        element: JsonElement,
//        config: DecoderConfig
//    ): JsonElement {
//
//
////        val fontSize = value.jsonObject[LabelStyleModelDomain.Name.fontSize]?.stringOrNull
////        val fontColor = value.jsonObject[LabelStyleModelDomain.Name.fontColor]?.stringOrNull
////
////
////        val idValue = database.idValue().find(
////            type = ColorModelDomain.Default.type,
////            url = config.url,
////            id = "palette",
////            key = fontSize!!.removePrefix(SymbolDomain.REF_INDICATOR)
////        )
//
//        return element
//    }
//
//}