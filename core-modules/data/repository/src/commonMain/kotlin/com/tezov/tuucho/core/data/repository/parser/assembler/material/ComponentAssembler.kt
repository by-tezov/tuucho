package com.tezov.tuucho.core.data.repository.parser.assembler.material

import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.scope.Scope

sealed class ComponentAssociation {
    object Matcher : ComponentAssociation()

    object Assembler : ComponentAssociation()
}

class ComponentAssembler(
    scope: Scope
) : AbstractAssembler(scope) {
    override val schemaType = TypeSchema.Value.component

    override val matchers: List<AssemblerMatcherProtocol> by lazy {
        scope.getAllAssociated(ComponentAssociation.Matcher::class)
    }

    override val childProcessors: List<AssemblerProtocol> by lazy {
        scope.getAllAssociated(ComponentAssociation.Assembler::class)
    }

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.component) || super.accept(path, element)
}
