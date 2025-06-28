package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ColorAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.Type.color

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.COLOR
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.COLOR
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) =  path.isTypeOf(element, TypeSchema.Value.Type.color) || super.accept(path, element)

}