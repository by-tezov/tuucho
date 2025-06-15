package com.tezov.tuucho.core.data.parser.rectifier.texts

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._schema.TextSchema
import com.tezov.tuucho.core.data.parser._schema.TextSchema.Companion.defaultPut
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idAddGroup
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idHasGroup
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idPut
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idPutNullIfMissing
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idRawOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idValueOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Rectifier
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.data.parser.rectifier.RectifierBase
import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

object TextRectifier : RectifierBase() {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.TEXT
    )

    private fun allAreTypeText(
        element: JsonArray
    ) = element.jsonArray.all {
        it.jsonObject[HeaderTypeSchema.Name.type].stringOrNull == TextSchema.Default.type
    }

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (path.lastSegment() == null && element is JsonArray && allAreTypeText(element))
            || super.accept(path, element)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path).jsonArray) {
        JsonArray(this.map { process("".toPath(), it) })
    }

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonObject.toMutableMap().apply {
        typePut(TextSchema.Default.type)
        idPutNullIfMissing()
    }.let(::JsonObject)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = mutableMapOf<String, JsonElement>()
        .apply {
            typePut(TextSchema.Default.type)
            idPutNullIfMissing()
            defaultPut(element.find(path).string)
        }
        .let(::JsonObject)

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path).jsonObject) {
        val (value, source) = rectifyIds(this)
        if (value != null || source != null) {
            this.toMutableMap().apply {
                (idRawOrNull as JsonObject).toMutableMap().apply {
                    idPut(value, source)
                }.let(::JsonObject)
            }.let(::JsonObject)
        } else null
    }

    private fun rectifyIds(current: JsonObject): Pair<String?, String?> {
        var value: String?
        var source: String?
        with(current) {
            with(idRawOrNull as JsonObject) {
                value = idValueOrNull?.takeIf { !it.idHasGroup() }
                    ?.idAddGroup(TextSchema.Default.Group.common)
                source = idSourceOrNull?.takeIf { !it.idHasGroup() }
                    ?.idAddGroup(TextSchema.Default.Group.common)
            }
        }
        return value to source
    }
}