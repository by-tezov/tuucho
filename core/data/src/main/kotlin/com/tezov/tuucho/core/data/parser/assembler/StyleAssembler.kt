package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.rectifier.StyleRectifier
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

class StyleAssembler : Assembler() {

    override val dataBaseType: String = TypeSchema.Value.Type.style

    override val matchers: List<Matcher> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<Assembler> by inject(
        Name.Processor.STYLE
    )

    private val rectifier: StyleRectifier by inject()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.Type.style) || super.accept(path, element)

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): List<JsonObject> {
        if (!any { it.subsetOrNull == SubsetSchema.Value.subset }) return this
        val parentSubset = (element.find(path.parent()) as JsonObject).subset
        return map { current ->
            if (current.subsetOrNull == SubsetSchema.Value.subset) {
                val altered = current.toMutableMap().apply {
                    subsetPut(parentSubset)
                }.let(::JsonObject)
                rectifier.process("".toPath(), altered) as JsonObject
            } else current
        }
    }

}