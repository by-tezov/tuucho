@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherRectifierProtocol {
    fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean
}
