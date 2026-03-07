@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.shadower._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface ShadowerProtocol : ShadowerMatcherProtocol {
    suspend fun FlowCollector<JsonObject>.process(
        path: JsonElementPath,
        element: JsonElement
    )
}
