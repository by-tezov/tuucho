package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

interface Breaker : Matcher, KoinComponent {
    val matchers: List<Matcher>
    val childProcessors: List<Breaker>

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath, element: JsonElement, extraData: ExtraDataBreaker
    ): JsonEntityElement

}