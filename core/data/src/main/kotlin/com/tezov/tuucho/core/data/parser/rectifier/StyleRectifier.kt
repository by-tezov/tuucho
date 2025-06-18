package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._schema.ComponentSchema
import com.tezov.tuucho.core.data.parser._schema.StyleSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema.Companion.idPutNullIfMissing
import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema.Companion.subsetForwardOrMarkUnknownMaybe
import com.tezov.tuucho.core.data.parser._schema.header.HeaderTypeSchema.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.parentIsTypeOf
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class StyleRectifier : RectifierBase() {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.STYLE
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (path.lastSegmentIs(StyleSchema.Default.type) && path.parentIsTypeOf(
        element, ComponentSchema.Default.type
    )) || super.accept(path, element)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonObject.toMutableMap().apply {
        idPutNullIfMissing()
        typePut(StyleSchema.Default.type)
        subsetForwardOrMarkUnknownMaybe(path, element)
    }.let(::JsonObject)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path).jsonArray) {
        JsonArray(this.map { process("".toPath(), it) })
    }
}