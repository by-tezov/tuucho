package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._schema.DimensionSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isTypeOf
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class DimensionAssembler : AssemblerBase() {

    override val dataBaseType: String = DimensionSchema.Default.type

    override val matchers: List<Matcher> by inject(
        Name.Matcher.DIMENSION
    )

    override val childProcessors: List<AssemblerBase> by inject(
        Name.Processor.DIMENSION
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, DimensionSchema.Default.type) || super.accept(path, element)

}