package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.rectifier.OptionRectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class OptionAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.option

    override val matchers: List<MatcherProtocol> by inject(
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
        val altered = schema().withScope(SubsetSchema::Scope).apply {
            self = parentSubset
        }.collect()
        return rectifier.process("".toPath(), altered) as JsonObject
    }

    override fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): JsonObject? {
        schema().withScope(SubsetSchema::Scope).self ?: return null
        val parentSubset = element.find(path.parent()).schema()
            .withScope(SubsetSchema::Scope).self!!
        return rectify(parentSubset)
    }

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): List<JsonObject>? {
        if (!any { it.schema().withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown }) return null
        val parentSubset = element.find(path.parent()).schema().withScope(SubsetSchema::Scope).self!!
        return map { current ->
            if (current.schema().withScope(SubsetSchema::Scope).self == SubsetSchema.Value.unknown) {
                current.rectify(parentSubset)
            } else current
        }
    }

}