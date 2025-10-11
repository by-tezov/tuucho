package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.StyleRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class StyleAssembler : AbstractAssembler() {

    override val schemaType: String = TypeSchema.Value.style

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<AbstractAssembler> by inject(
        Name.Processor.STYLE
    )

    private val rectifier: StyleRectifier by inject()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.style) || super.accept(path, element)

    private fun JsonObject.rectify(
        parentSubset: String
    ): JsonObject {
        val altered = withScope(SubsetSchema::Scope).apply {
            self = parentSubset
        }.collect()
        return rectifier.process("".toPath(), altered) as JsonObject
    }

    override fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement
    ): JsonObject? {
        withScope(SubsetSchema::Scope).self ?: return null
        val parentSubset = element.find(path.parent())
            .withScope(SubsetSchema::Scope).self
        require(parentSubset != null) { "parentSubset is null" }
        return rectify(parentSubset)
    }

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement
    ): List<JsonObject>? {
        if (!any { it.withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown }) return null
        val parentSubset = element.find(path.parent())
            .withScope(SubsetSchema::Scope).self
        require(parentSubset != null) { "parentSubset is null" }
        return map { current ->
            if (current.withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown) {
                current.rectify(parentSubset)
            } else current
        }
    }

}