package com.tezov.tuucho.core.domain.model._system

import com.tezov.tuucho.core.domain.model.material.ValueOrObjectModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderHasChildrenModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderSubsetModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderTypeModelDomain
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun JsonObject.isRefModelDomain(): Boolean = with(
    this
            - HeaderTypeModelDomain.Name.type
            - HeaderHasChildrenModelDomain.Name.hasChildren
            - HeaderSubsetModelDomain.Name.subset
) {
//    if (size == 1) {
//        val idIdRef =
//            this[HeaderIdsModelDomain.Name.id]?.stringOrNull?.startsWith(SymbolDomain.REF_INDICATOR) == true
//        val isIdFrom = this[HeaderIdsModelDomain.Name.idFrom]?.stringOrNull != null
//        return@with idIdRef || isIdFrom
//    }
    return false
}

val Decoder.jsonDecoder get() = this as? JsonDecoder ?: error("only Json decoding is supported")
val Decoder.jsonElement get() = this.jsonDecoder.decodeJsonElement()
val Decoder.jsonObject get() = this.jsonDecoder.jsonElement.jsonObject

val Encoder.jsonEncoder get() = this as? JsonEncoder ?: error("only Json encoding is supported")

val JsonElement?.stringOrNull get() = this?.jsonPrimitive?.contentOrNull
val JsonElement?.string get() = this!!.jsonPrimitive.content

val JsonElement?.booleanOrNull: Boolean? get() = this?.jsonPrimitive?.boolean
val JsonElement?.boolean get() = this!!.jsonPrimitive.boolean

fun <T : ValueOrObjectModelDomain> Map<String, JsonElement>.mapValueOrObjectNullable(
    json: Json,
    serializer: KSerializer<T>
): Map<String, T?> = this.asSequence().associate { entry ->
    entry.key to json.decodeFromJsonElement(serializer, entry.value)
}
