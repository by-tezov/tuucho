package com.tezov.tuucho.core.data.network.service

import com.tezov.tuucho.core.data.network.response.JsonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MaterialNetworkHttpRequest {

    @GET("resource?version=v1")
    suspend fun retrieve(@Query("name") url: String): JsonResponse

}