package com.tezov.tuucho.core.data.network.response

import com.tezov.tuucho.core.data.network.response.JsonResponse.CallAdapterFactory.Callback.Companion.toMaterialsResponse
import okhttp3.ResponseBody
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class JsonResponse(
    val url: String,
    val code: Int,
    val json: String?,
) {

    internal class CallAdapterFactory : CallAdapter.Factory() {

        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): retrofit2.CallAdapter<*, *>? {
            if (getRawType(returnType) != retrofit2.Call::class.java) return null
            val responseType =
                (returnType as? ParameterizedType)?.actualTypeArguments?.firstOrNull()
            if (responseType != JsonResponse::class.java) return null
            return CallAdapter()
        }

        class CallAdapter :
            retrofit2.CallAdapter<ResponseBody, retrofit2.Call<JsonResponse>> {

            override fun responseType(): Type = ResponseBody::class.java

            override fun adapt(call: retrofit2.Call<ResponseBody>) = Call(call)
        }

        class Call(
            private val call: retrofit2.Call<ResponseBody>
        ) : retrofit2.Call<JsonResponse> {

            override fun enqueue(callback: retrofit2.Callback<JsonResponse>) {
                call.enqueue(
                    Callback(
                        this,
                        callback
                    )
                )
            }

            override fun execute() = call.execute().toMaterialsResponse()

            override fun clone(): retrofit2.Call<JsonResponse> =
                Call(
                    call.clone()
                )

            override fun cancel() = call.cancel()

            override fun request() = call.request()

            override fun timeout() = call.timeout()

            override fun isExecuted() = call.isExecuted

            override fun isCanceled() = call.isCanceled
        }

        class Callback(
            private val call: retrofit2.Call<JsonResponse>,
            private val callback: retrofit2.Callback<JsonResponse>
        ) : retrofit2.Callback<ResponseBody> {

            companion object {

                fun Response<ResponseBody>.toMaterialsResponse(): Response<JsonResponse> {
                    return Response.success(
                        JsonResponse(
                            url = this.raw().request.url.toString(),
                            code = this.code(),
                            json = this.body()?.string()
                        )
                    )
                }
            }

            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (!response.isSuccessful) {
                    onFailure(call, IOException("response is not successful ${call.request().url}"))
                } else {
                    callback.onResponse(
                        this.call,
                        response.toMaterialsResponse()
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, throwable: Throwable) {
                callback.onFailure(this.call, throwable)
            }
        }
    }

}


