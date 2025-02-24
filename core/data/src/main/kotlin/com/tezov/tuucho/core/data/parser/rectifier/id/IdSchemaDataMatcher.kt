package com.tezov.tuucho.core.data.parser.rectifier.id

import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser._schema.ColorSchemaData
import com.tezov.tuucho.core.data.parser._schema.ComponentSchemaData
import com.tezov.tuucho.core.data.parser._schema.ContentSchemaData
import com.tezov.tuucho.core.data.parser._schema.DimensionSchemaData
import com.tezov.tuucho.core.data.parser._schema.OptionSchemaData
import com.tezov.tuucho.core.data.parser._schema.StyleSchemaData
import com.tezov.tuucho.core.data.parser._schema.TextSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent

object IdSchemaDataMatcher {

    val lists: List<SchemaDataMatcher>
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
    ) = path.lastSegment() == HeaderIdSchemaData.Name.id

    private fun isInsideType(
        path: JsonElementPath, element: JsonElement, value: String
    ) = element.find(path.parent())
        .jsonObject[HeaderTypeSchemaData.Name.type].stringOrNull == value

    object Component : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, ComponentSchemaData.Default.type)
    }

    object Option : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, OptionSchemaData.Default.type)
    }

    object Style : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, StyleSchemaData.Default.type)
    }

    object Content : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, ContentSchemaData.Default.type)
    }

    object Text : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, TextSchemaData.Default.type)
    }

    object Color : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, ColorSchemaData.Default.type)
    }

    object Dimension : SchemaDataMatcher, KoinComponent {

        override fun accept(
            path: JsonElementPath, element: JsonElement
        ) = isId(path) && isInsideType(path, element, DimensionSchemaData.Default.type)
    }

}