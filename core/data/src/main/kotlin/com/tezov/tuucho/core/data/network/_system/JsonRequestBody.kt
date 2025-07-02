package com.tezov.tuucho.core.data.network._system

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

data class JsonRequestBody(
    val json: String,
) {

    internal class ConverterFactory : Converter.Factory() {

        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<out Annotation>,
            methodAnnotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<*, RequestBody>? {
            if (type == JsonRequestBody::class.java) {
                return JsonConverter()
            }
            return null
        }

        class JsonConverter : Converter<JsonRequestBody, RequestBody> {
            override fun convert(value: JsonRequestBody): RequestBody {
                return value.json.toRequestBody("application/json".toMediaType())
            }
        }
    }

}


