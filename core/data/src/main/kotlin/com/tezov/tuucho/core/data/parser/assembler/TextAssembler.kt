package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._schema.TextSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isTypeOf
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class TextAssembler : AssemblerBase() {

    override val dataBaseType: String = TextSchema.Default.type

    override val matchers: List<Matcher> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<AssemblerBase> by inject(
        Name.Processor.TEXT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TextSchema.Default.type) || super.accept(path, element)

}