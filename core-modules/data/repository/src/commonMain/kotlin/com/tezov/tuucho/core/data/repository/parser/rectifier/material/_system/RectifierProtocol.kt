@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.material._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface RectifierProtocol : RectifierMatcherProtocol {
    val key: String

    fun process(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement
}
