package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.rectifier.ContentRectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.withScope

import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ContentAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.content

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.CONTENT
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.CONTENT
    )

    private val rectifier: ContentRectifier by inject()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.content) || super.accept(path, element)

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
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): JsonObject? {
        withScope(SubsetSchema::Scope).self ?: return null
        val parentSubset = element.find(path.parent())
            .withScope(SubsetSchema::Scope).self!!
        return rectify(parentSubset)
    }

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
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