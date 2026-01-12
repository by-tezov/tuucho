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

class ComponentAssembler(
    scope: Scope
) : AbstractAssembler(scope) {

    sealed class Association {
        object Matcher : Association()

        object Processor : Association()
    }

    override val schemaType = TypeSchema.Value.component

    override val matchers: List<AssemblerMatcherProtocol> by lazy {
        scope.getAllAssociated(Association.Matcher::class)
    }

    override val childProcessors: List<AssemblerProtocol> by lazy {
        scope.getAllAssociated(Association.Processor::class)
    }

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.component) || super.accept(path, element)
}
