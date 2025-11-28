package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler._system.AbstractAssembler
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

@OptIn(TuuchoExperimentalAPI::class)
class DimensionAssembler : AbstractAssembler() {
    override val schemaType = TypeSchema.Value.dimension

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.dimension) || super.accept(path, element)
}
