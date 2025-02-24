package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._schema.ComponentSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPutNullIfMissing
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

object ComponentSchemaDataRectifier : SchemaDataRectifier() {

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.COMPONENT
    )
    override val childProcessors: List<SchemaDataRectifier> by inject(
        Name.Processor.COMPONENT
    )

    override fun beforeAlterObject(path: JsonElementPath, element: JsonElement) =
        element.find(path).jsonObject.toMutableMap().apply {
            idPutNullIfMissing()
            typePut(ComponentSchemaData.Default.type)
        }.let(::JsonObject)

    override fun beforeAlterArray(path: JsonElementPath, element: JsonElement) =
        with(element.find(path).jsonArray) {
            JsonArray(this.map { process("".toPath(), it) })
        }

}