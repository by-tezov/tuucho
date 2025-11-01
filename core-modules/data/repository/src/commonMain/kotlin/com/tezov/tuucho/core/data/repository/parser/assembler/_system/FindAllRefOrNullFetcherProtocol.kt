@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler._system

import kotlinx.serialization.json.JsonObject

fun interface FindAllRefOrNullFetcherProtocol {
    suspend operator fun invoke(
        from: JsonObject,
        type: String
    ): List<JsonObject>?
}
