package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.findOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class IdMatcher : MatcherProtocol, KoinComponent {

    private val types = listOf(
        TypeSchema.Value.Type.component,
        TypeSchema.Value.Type.style,
        TypeSchema.Value.Type.content,
        TypeSchema.Value.Type.text,
        TypeSchema.Value.Type.color,
        TypeSchema.Value.Type.dimension
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(IdSchema.Key.id)) return false
        val parentType = element.findOrNull(path.parent())
            ?.let { it as? JsonObject }
            ?.typeOrNull ?: return false
        return types.any { it == parentType }


    }


}