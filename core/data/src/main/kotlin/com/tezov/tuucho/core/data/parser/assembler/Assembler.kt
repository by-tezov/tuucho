package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

interface Assembler : Matcher, KoinComponent {
    val dataBaseType: String

    val matchers: List<Matcher>
    val childProcessors: List<Assembler>

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    suspend fun process(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): JsonElement

}