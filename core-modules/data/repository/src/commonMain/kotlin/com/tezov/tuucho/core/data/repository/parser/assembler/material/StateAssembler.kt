package com.tezov.tuucho.core.data.repository.parser.assembler.material

import com.tezov.tuucho.core.data.repository.di.assembler.AssemblerModule
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.state.StateRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class StateAssembler(
    scope: Scope
) : AbstractAssembler(scope) {
    override val schemaType = TypeSchema.Value.state

    override val childProcessors: List<AbstractAssembler> by inject(
        AssemblerModule.Name.Processor.STATE
    )

    private val rectifier: StateRectifier by inject()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.state)

    private fun JsonObject.rectify(
        parentSubset: String
    ): JsonObject {
        val altered = withScope(SubsetSchema::Scope)
            .apply {
                self = parentSubset
            }.collect()
        return rectifier.process(ROOT_PATH, altered) as JsonObject
    }

    override fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement
    ): JsonObject? {
        withScope(SubsetSchema::Scope).self ?: return null
        val parentSubset = element
            .find(path.parent())
            .withScope(SubsetSchema::Scope)
            .self
        require(parentSubset != null) { "parentSubset is null" }
        return rectify(parentSubset)
    }

    override fun List<JsonObject>.rectify(
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
                current.rectify(parentSubset)
            } else {
                current
            }
        }
    }
}
