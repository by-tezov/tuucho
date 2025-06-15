package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.parser._schema.ColorSchema
import com.tezov.tuucho.core.data.parser._schema.ComponentSchema
import com.tezov.tuucho.core.data.parser._schema.ContentSchema
import com.tezov.tuucho.core.data.parser._schema.DimensionSchema
import com.tezov.tuucho.core.data.parser._schema.OptionSchema
import com.tezov.tuucho.core.data.parser._schema.StyleSchema
import com.tezov.tuucho.core.data.parser._schema.TextSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
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

    private fun isId(
        path: JsonElementPath
    ) = path.lastSegment() == HeaderIdSchema.Name.id

    private fun isInsideType(
        path: JsonElementPath, element: JsonElement, value: String
    ) = element.find(path.parent())
        .jsonObject[HeaderTypeSchema.Name.type].stringOrNull == value

    object Component : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, ComponentSchema.Default.type)
    }

    object Option : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, OptionSchema.Default.type)
    }

    object Style : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, StyleSchema.Default.type)
    }

    object Content : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, ContentSchema.Default.type)
    }

    object Text : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, TextSchema.Default.type)
    }

    object Color : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, ColorSchema.Default.type)
    }

    object Dimension : Matcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, DimensionSchema.Default.type)
    }

}