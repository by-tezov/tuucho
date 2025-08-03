package com.tezov.tuucho.core.data.parser.rectifier.id


import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.parentIsAnyTypeOf
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

class IdMatcher : MatcherRectifierProtocol, KoinComponent {

    private val types = listOf(
        TypeSchema.Value.component,
        TypeSchema.Value.content,
        TypeSchema.Value.style,
        TypeSchema.Value.option,
        TypeSchema.Value.text,
        TypeSchema.Value.color,
        TypeSchema.Value.dimension
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement,
    ) = path.lastSegmentIs(IdSchema.root) && path.parentIsAnyTypeOf(element, types)

}