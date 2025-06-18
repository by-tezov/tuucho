package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

interface Rectifier : Matcher, KoinComponent {
    val matchers: List<Matcher>
    val childProcessors: List<Rectifier>

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath, element: JsonElement
    ): JsonElement
}