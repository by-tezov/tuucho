package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class TextAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.text

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.TEXT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.text) || super.accept(path, element)

}