package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._schema.ComponentSchema
import com.tezov.tuucho.core.data.parser._schema.StyleSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idPutNullIfMissing
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchema.Companion.subsetForwardIfNotNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Rectifier
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

object StyleRectifier : RectifierBase() {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.STYLE
    )

    private fun isStyle(
        path: JsonElementPath
    ) = path.lastSegment() == StyleSchema.Default.type

    private fun isInsideTypeComponent(
        path: JsonElementPath, element: JsonElement
    ) = element.find(path.parent())
        .jsonObject[HeaderTypeSchema.Name.type].stringOrNull == ComponentSchema.Default.type

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (isStyle(path) && isInsideTypeComponent(path, element))
            || super.accept(path, element)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonObject.toMutableMap().apply {
        idPutNullIfMissing()
        typePut(StyleSchema.Default.type)
        subsetForwardIfNotNull(path, element)
    }.let(::JsonObject)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path).jsonArray) {
        JsonArray(this.map { process("".toPath(), it) })
    }
}