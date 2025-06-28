package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.rectifier.ContentRectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subset
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetPut
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ContentAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.Type.content

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.CONTENT
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.CONTENT
    )

    private val rectifier: ContentRectifier by inject()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.Type.content) || super.accept(path, element)

    private fun JsonObject.rectify(
        parentSubset: String
    ): JsonObject {
        val altered = toMutableMap().apply {
            subsetPut(parentSubset)
        }.let(::JsonObject)
        return rectifier.process("".toPath(), altered) as JsonObject
    }

    override fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): JsonObject? {
        if (subsetOrNull != SubsetSchema.Value.Subset.unknown) return null
        val parentSubset = (element.find(path.parent()) as JsonObject).subset
        return rectify(parentSubset)
    }

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): List<JsonObject>? {
        if (!any { it.subsetOrNull == SubsetSchema.Value.Subset.unknown }) return null
        val parentSubset = (element.find(path.parent()) as JsonObject).subset
        return map { current ->
            if (current.subsetOrNull == SubsetSchema.Value.Subset.unknown) {
                current.rectify(parentSubset)
            } else current
        }
    }

}