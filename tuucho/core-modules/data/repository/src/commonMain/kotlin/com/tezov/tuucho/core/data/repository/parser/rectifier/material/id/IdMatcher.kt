package com.tezov.tuucho.core.data.repository.parser.rectifier.material.id

import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser._system.parentIsAnyTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

class IdMatcher :
    RectifierMatcherProtocol,
    TuuchoKoinComponent {
    private val types = listOf(
        TypeSchema.Value.component,
        TypeSchema.Value.content,
        TypeSchema.Value.style,
        TypeSchema.Value.option,
        TypeSchema.Value.state,
        TypeSchema.Value.text,
        TypeSchema.Value.color,
        TypeSchema.Value.dimension,
        TypeSchema.Value.action,
        TypeSchema.Value.Setting.component,
        TypeSchema.Value.Setting.page,
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = path.lastSegmentIs(IdSchema.root) && path.parentIsAnyTypeOf(element, types)
}
