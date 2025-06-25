package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import org.koin.core.component.inject

class ComponentAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.Type.component

    override val matchers: List<Matcher> by inject(
        Name.Matcher.COMPONENT
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.COMPONENT
    )
}