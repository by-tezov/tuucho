package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ActionAssembler : AbstractAssembler() {
    override val schemaType: String = TypeSchema.Value.action

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.ACTION
    )

    override val childProcessors: List<AbstractAssembler> by inject(
        Name.Processor.ACTION
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.action) || super.accept(path, element)
}
