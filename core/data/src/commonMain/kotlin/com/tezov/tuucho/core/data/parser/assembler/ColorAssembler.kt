package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name

import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ColorAssembler : Assembler() {

    override val schemaType: String = TypeSchema.Value.color

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.COLOR
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.COLOR
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) =  path.isTypeOf(element, TypeSchema.Value.color) || super.accept(path, element)

}