package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.parser._schema.StyleSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema.Companion.subset
import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema.Companion.subsetPut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isTypeOf
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.data.parser.rectifier.StyleRectifier
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class StyleAssembler : AssemblerBase() {

    override val dataBaseType: String = StyleSchema.Default.type

    override val matchers: List<Matcher> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<AssemblerBase> by inject(
        Name.Processor.STYLE
    )

    private val rectifier: StyleRectifier by inject()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, StyleSchema.Default.type) || super.accept(path, element)

    override fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler
    ): List<JsonObject> {
        if (!any { it.subsetOrNull == HeaderSubsetSchema.Default.subset }) return this
        val parentSubset = (element.find(path.parent()) as JsonObject).subset
        return map { current ->
            if (current.subsetOrNull == HeaderSubsetSchema.Default.subset) {
                val altered = current.toMutableMap().apply {
                    subsetPut(parentSubset)
                }.let(::JsonObject)
                rectifier.process("".toPath(), altered) as JsonObject
            } else current
        }
    }

}