package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

@OptIn(TuuchoExperimentalAPI::class)
class ComponentAssembler : AbstractAssembler() {
    override val schemaType = TypeSchema.Value.component

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.COMPONENT
    )

    override val childProcessors: List<AbstractAssembler> by inject(
        Name.Processor.COMPONENT
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.component) || super.accept(path, element)
}
