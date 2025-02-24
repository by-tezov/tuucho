package com.tezov.tuucho.data.network.service

import com.tezov.tuucho.data.network.response.PageDataResponse

interface PageNetworkService {

    @GET("string-endpoint")
    suspend fun retrieve(name: String): PageDataResponse

}