package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._schema.ColorSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isTypeOf
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ColorBreaker: BreakerBase() {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.COLOR
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.COLOR
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, ColorSchema.Default.type) || super.accept(path, element)

}