package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idPutNullIfMissing
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetForwardOrMarkUnknownMaybe
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typePut
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class StyleRectifier : Rectifier() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.STYLE
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (path.lastSegmentIs(TypeSchema.Value.Type.style) && path.parentIsTypeOf(
        element, TypeSchema.Value.Type.component
    )) || super.accept(path, element)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonObject.toMutableMap().apply {
        idPutNullIfMissing()
        typePut(TypeSchema.Value.Type.style)
        subsetForwardOrMarkUnknownMaybe(path, element)
    }.let(::JsonObject)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path).jsonArray) {
        JsonArray(this.map { process("".toPath(), it) })
    }
}