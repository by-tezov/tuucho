package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.common.IdSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

object IdMatcher {

    val lists: List<MatcherProtocol>
        get() = listOf(
            Component,
            Style,
            Content,
            Text,
            Color,
            Dimension,
        )

    private fun JsonElementPath.isId() = lastSegmentIs(IdSchema.Key.id)

    object Component : MatcherProtocol, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.component)
    }

    object Style : MatcherProtocol, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.style)
    }

    object Content : MatcherProtocol, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.content)
    }

    object Text : MatcherProtocol, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.text)
    }

    object Color : MatcherProtocol, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.color)
    }

    object Dimension : MatcherProtocol, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.dimension)
    }

}