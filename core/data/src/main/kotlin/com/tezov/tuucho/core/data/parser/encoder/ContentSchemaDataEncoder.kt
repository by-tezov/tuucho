package com.tezov.tuucho.core.data.parser.encoder

import com.tezov.tuucho.core.data.di.MaterialEncoderModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataEncoder
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser._schema.ComponentSchemaData
import com.tezov.tuucho.core.data.parser._schema.DefaultComponentSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ContentSchemaDataEncoder: SchemaDataEncoder(), KoinComponent {

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.CONTENT
    )

    override val childProcessors: List<SchemaDataEncoder> by inject(
        Name.Processor.CONTENT
    )

    private fun isContent(
        path: JsonElementPath
    ) = path.lastSegment() == DefaultComponentSchemaData.Name.content

    private fun isInsideTypeComponent(
        path: JsonElementPath, element: JsonElement
    ) = element.find(path.parent())
        .jsonObject[HeaderTypeSchemaData.Name.type].stringOrNull == ComponentSchemaData.Default.type

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (isContent(path) && isInsideTypeComponent(path, element))
            || super.accept(path, element)
}