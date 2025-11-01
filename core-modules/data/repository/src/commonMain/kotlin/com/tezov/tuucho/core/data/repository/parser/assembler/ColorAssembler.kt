package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ColorAssembler : AbstractAssembler() {
    override val schemaType: String = TypeSchema.Value.color

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.COLOR
    )

    override val childProcessors: List<AbstractAssembler> by inject(
        Name.Processor.COLOR
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.color) || super.accept(path, element)
}
