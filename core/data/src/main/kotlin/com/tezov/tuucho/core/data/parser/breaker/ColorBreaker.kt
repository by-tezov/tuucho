package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._schema.ColorSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import com.tezov.tuucho.core.data.parser._system.Breaker
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ColorBreaker: BreakerBase(), KoinComponent {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.COLOR
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.COLOR
    )

    private fun isColor(
        path: JsonElementPath, element: JsonElement
    ) = (element.find(path) as? JsonObject)
        ?.get(HeaderTypeSchema.Name.type)
        ?.let { it.stringOrNull == ColorSchema.Default.type }
        ?: false


    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = isColor(path, element) || super.accept(path, element)

}