package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.data.parser.rectifier.option.OptionRectifier
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class OptionAssembler : Assembler() {

    override val schemaType: String = TypeSchema.Value.option

    override val matchers: List<MatcherAssemblerProtocol> by inject(
        Name.Matcher.OPTION
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.OPTION
    )

    private val rectifier: OptionRectifier by inject()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.option) || super.accept(path, element)

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
            .withScope(SubsetSchema::Scope).self!!
        return rectify(parentSubset)
    }

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement
    ): List<JsonObject>? {
        if (!any { it.withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown }) return null
        val parentSubset = element.find(path.parent()).withScope(SubsetSchema::Scope).self!!
        return map { current ->
            if (current.withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown) {
                current.rectify(parentSubset)
            } else current
        }
    }

}