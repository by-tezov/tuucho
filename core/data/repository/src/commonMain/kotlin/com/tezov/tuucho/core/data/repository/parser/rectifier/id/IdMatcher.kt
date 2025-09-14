package com.tezov.tuucho.core.data.repository.parser.rectifier.id


import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser._system.parentIsAnyTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
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