@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.shadower._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface ShadowerProtocol : ShadowerMatcherProtocol {
    suspend fun process(
        path: JsonElementPath,
        element: JsonElement,
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    )
}
