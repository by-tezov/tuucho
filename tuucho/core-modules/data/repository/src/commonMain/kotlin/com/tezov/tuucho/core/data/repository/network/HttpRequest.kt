package com.tezov.tuucho.core.data.repository.network

import kotlinx.serialization.json.JsonObject

internal data class HttpRequest(
    val url: String,
    val jsonObject: JsonObject? = null
)
