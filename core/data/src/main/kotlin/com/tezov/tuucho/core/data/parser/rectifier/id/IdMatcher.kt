package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.parser._schema.ColorSchema
import com.tezov.tuucho.core.data.parser._schema.ComponentSchema
import com.tezov.tuucho.core.data.parser._schema.ContentSchema
import com.tezov.tuucho.core.data.parser._schema.DimensionSchema
import com.tezov.tuucho.core.data.parser._schema.OptionSchema
import com.tezov.tuucho.core.data.parser._schema.StyleSchema
import com.tezov.tuucho.core.data.parser._schema.TextSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.parentIsTypeOf
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

object IdMatcher {

    val lists: List<Matcher>
        get() = listOf(
            Component,
            Option,
            Style,
            Content,
            Text,
            Color,
            Dimension,
        )

    private fun JsonElementPath.isId() = lastSegmentIs(HeaderIdSchema.Name.id)

    object Component : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, ComponentSchema.Default.type)
    }

    object Option : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, OptionSchema.Default.type)
    }

    object Style : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, StyleSchema.Default.type)
    }

    object Content : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, ContentSchema.Default.type)
    }

    object Text : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, TextSchema.Default.type)
    }

    object Color : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, ColorSchema.Default.type)
    }

    object Dimension : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = path.isId() && path.parentIsTypeOf(element, DimensionSchema.Default.type)
    }

}