package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._schema.StyleSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isTypeOf
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class StyleBreaker : BreakerBase() {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.STYLE
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, StyleSchema.Default.type) || super.accept(path, element)

}