package com.tezov.tuucho.core.data.network.service

import com.tezov.tuucho.core.data.network._system.JsonRequestBody
import com.tezov.tuucho.core.data.network._system.JsonResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MaterialNetworkHttpRequest(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun retrieve(name: String): JsonResponse {
        val response = client.get("$baseUrl/resource") {
            parameter("version", "v1")
            parameter("name", name)
        }
        return JsonResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            json = response.bodyAsText()
        )
    }

    suspend fun send(name: String, jsonRequestBody: JsonRequestBody): JsonResponse {
        val response = client.post("$baseUrl/send") {
            parameter("version", "v1")
            parameter("name", name)
            contentType(ContentType.Application.Json)
            setBody(jsonRequestBody.json)
        }
        return JsonResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            json = response.bodyAsText()
        )
    }
}