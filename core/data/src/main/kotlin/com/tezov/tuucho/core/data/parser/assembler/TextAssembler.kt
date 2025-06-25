package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class TextAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.Type.text

    override val matchers: List<Matcher> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.TEXT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element,TypeSchema.Value.Type.text) || super.accept(path, element)

}