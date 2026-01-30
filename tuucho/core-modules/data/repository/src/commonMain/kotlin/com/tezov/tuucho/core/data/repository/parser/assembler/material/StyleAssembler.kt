package com.tezov.tuucho.core.data.repository.parser.assembler.material

import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol.Context.Companion.toContextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.style.StyleRectifier
import com.tezov.tuucho.core.domain.business._system.koin.Associate.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class StyleAssembler(
    scope: Scope
) : AbstractAssembler(scope) {
    sealed class Association {
        object Processor : Association()
    }

    override val schemaType = TypeSchema.Value.style

    override val childProcessors: List<AssemblerProtocol> by lazy {
        scope.getAllAssociated(Association.Processor::class)
    }

    private val rectifier: StyleRectifier by inject()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.style)

    private fun JsonObject.rectify(
        context: RectifierProtocol.Context,
        parentSubset: String
    ): JsonObject {
        val altered = withScope(SubsetSchema::Scope)
            .apply {
                self = parentSubset
            }.collect()
        return rectifier.process(context, ROOT_PATH, altered) as JsonObject
    }

    override fun JsonObject.rectify(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonObject? {
        withScope(SubsetSchema::Scope).self ?: return null
        val parentSubset = element
            .find(path.parent())
            .withScope(SubsetSchema::Scope)
            .self
        require(parentSubset != null) { "parentSubset is null" }
        return rectify(
            context = context.toContextRectifier(),
            parentSubset = parentSubset
        )
    }

    override fun List<JsonObject>.rectify(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): List<JsonObject>? {
        if (!any { it.withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown }) return null
        val parentSubset = element
            .find(path.parent())
            .withScope(SubsetSchema::Scope)
            .self
        require(parentSubset != null) { "parentSubset is null" }
        return map { current ->
            if (current.withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown) {
                current.rectify(
                    context = context.toContextRectifier(),
                    parentSubset = parentSubset
                )
            } else {
                current
            }
        }
    }
}
