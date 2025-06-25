package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.common.IdSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

object IdMatcher {

    val lists: List<Matcher>
        get() = listOf(
            Component,
            Style,
            Content,
            Text,
            Color,
            Dimension,
        )

    private fun JsonElementPath.isId() = lastSegmentIs(IdSchema.Key.id)

    object Component : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.component)
    }

    object Style : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.style)
    }

    object Content : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.content)
    }

    object Text : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.text)
    }

    object Color : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.color)
    }

    object Dimension : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TypeSchema.Value.Type.dimension)
    }

}