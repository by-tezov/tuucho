package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._schema.ComponentSchema
import com.tezov.tuucho.core.data.parser._system.Matcher
import org.koin.core.component.inject

class ComponentAssembler : AssemblerBase() {

    override val dataBaseType: String = ComponentSchema.Default.type

    override val matchers: List<Matcher> by inject(
        Name.Matcher.COMPONENT
    )

    override val childProcessors: List<AssemblerBase> by inject(
        Name.Processor.COMPONENT
    )
}