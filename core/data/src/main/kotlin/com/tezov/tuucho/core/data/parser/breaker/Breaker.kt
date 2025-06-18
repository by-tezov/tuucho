package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.data.parser._system.Matcher
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

interface Breaker : Matcher, KoinComponent {
    val matchers: List<Matcher>
    val childProcessors: List<Breaker>

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataBreaker
    ): JsonEntityElement

}