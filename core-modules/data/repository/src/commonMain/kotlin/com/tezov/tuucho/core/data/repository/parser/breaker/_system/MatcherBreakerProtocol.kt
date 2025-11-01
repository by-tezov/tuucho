@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.breaker._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherBreakerProtocol {
    fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean
}
