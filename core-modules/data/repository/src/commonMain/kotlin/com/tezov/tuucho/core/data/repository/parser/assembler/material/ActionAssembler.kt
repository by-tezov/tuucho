package com.tezov.tuucho.core.data.repository.parser.assembler.material

import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class ActionAssembler(
    scope: Scope
) : AbstractAssembler(scope) {
    override val schemaType = TypeSchema.Value.action

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.action) || super.accept(path, element)
}
