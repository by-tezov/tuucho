package com.tezov.tuucho.core.data.parser.rectifier.colors

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._schema.ColorSchemaData
import com.tezov.tuucho.core.data.parser._schema.TextSchemaData.Companion.defaultPut
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idAddGroup
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idHasGroup
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPut
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPutNullIfMissing
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idRawOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idValueOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

object ColorSchemaDataRectifier : SchemaDataRectifier() {

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.COLOR
    )
    override val childProcessors: List<SchemaDataRectifier> by inject(
        Name.Processor.COLOR
    )

    private fun allAreTypeColor(
        element: JsonArray
    ) = element.jsonArray.all {
        it.jsonObject[HeaderTypeSchemaData.Name.type].stringOrNull == ColorSchemaData.Default.type
    }

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (path.lastSegment() == null && element is JsonArray && allAreTypeColor(element))
            || super.accept(path, element)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement {
        return JsonArray(element.find(path).jsonArray.map { process("".toPath(), it) })
    }

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonObject.toMutableMap().apply {
        typePut(ColorSchemaData.Default.type)
    }.let(::JsonObject)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = mutableMapOf<String, JsonElement>()
        .apply {
            idPutNullIfMissing()
            defaultPut(element.find(path).string)
        }.let(::JsonObject)

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
                    ?.idAddGroup(ColorSchemaData.Default.Group.common)
                source = idSourceOrNull?.takeIf { !it.idHasGroup() }
                    ?.idAddGroup(ColorSchemaData.Default.Group.common)
            }
        }
        return value to source
    }
}