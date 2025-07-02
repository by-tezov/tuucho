package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class DimensionAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.Type.dimension

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.DIMENSION
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.DIMENSION
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.Type.dimension) || super.accept(path, element)

}