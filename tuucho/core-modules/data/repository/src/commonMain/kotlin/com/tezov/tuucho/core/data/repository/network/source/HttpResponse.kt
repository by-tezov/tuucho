package com.tezov.tuucho.core.data.repository.network.source

import kotlinx.serialization.json.JsonObject

internal data class HttpResponse(
    val url: String,
    val code: Int,
    val jsonObject: JsonObject?,
)
