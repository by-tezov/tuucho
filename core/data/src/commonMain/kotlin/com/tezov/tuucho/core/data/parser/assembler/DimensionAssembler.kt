package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class DimensionAssembler : Assembler() {

    override val schemaType: String = TypeSchema.Value.dimension

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.DIMENSION
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.DIMENSION
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.dimension) || super.accept(path, element)

}