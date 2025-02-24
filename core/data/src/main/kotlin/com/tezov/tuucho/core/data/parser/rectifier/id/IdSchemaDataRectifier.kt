package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPut
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idValueOrNull
import com.tezov.tuucho.core.data.parser._system.IdGenerator
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.SymbolDomain
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

object IdSchemaDataRectifier : SchemaDataRectifier() {

    private val idGenerator: IdGenerator by inject()

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.ID
    )

    override fun beforeAlterNull(path: JsonElementPath, element: JsonElement) =
        mutableMapOf<String, JsonElement>().apply {
            val (id, idFrom) = rectifyIds(null, null)
            idPut(id, idFrom)
        }.let(::JsonObject)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = mutableMapOf<String, JsonElement>().apply {
        val (id, idFrom) = rectifyIds(element.find(path).stringOrNull, null)
        idPut(id, idFrom)
    }.let(::JsonObject)

    override fun beforeAlterObject(path: JsonElementPath, element: JsonElement) =
        element.find(path).jsonObject.toMutableMap().apply {
            val (id, idFrom) = rectifyIds(idValueOrNull, idSourceOrNull)
            idPut(id, idFrom)
        }.let(::JsonObject)

    private fun rectifyIds(id: String?, idFrom: String?): Pair<String, String?> {
        if (id == null || id.startsWith(SymbolDomain.ID_REF_INDICATOR)) {
            return idGenerator.generate() to id?.removePrefix(SymbolDomain.ID_REF_INDICATOR)
        }
        return id to idFrom?.removePrefix(SymbolDomain.ID_REF_INDICATOR)
    }
}