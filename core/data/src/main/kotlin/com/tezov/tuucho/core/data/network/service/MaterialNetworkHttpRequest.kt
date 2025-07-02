package com.tezov.tuucho.core.data.network.service

import com.tezov.tuucho.core.data.network._system.JsonRequestBody
import com.tezov.tuucho.core.data.network._system.JsonResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MaterialNetworkHttpRequest {

    @GET("resource?version=v1")
    suspend fun retrieve(@Query("name") url: String): JsonResponse

    @POST("send?version=v1")
    suspend fun send(@Query("name") url: String, @Body jsonRequestBody: JsonRequestBody): JsonResponse?

}