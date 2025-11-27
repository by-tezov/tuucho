@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.field.content

import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.text.TextRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

@OptIn(TuuchoExperimentalAPI::class)
class ContentFormFieldTextErrorRectifier : AbstractRectifier() {
    override val key = FormFieldSchema.Content.Key.messageError
    private val matcher = ContentFormFieldTextErrorMatcher()
    private val textRectifier: TextRectifier by inject()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList { add(element.find(path)) }.let(::JsonArray)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList { add(element.find(path)) }.let(::JsonArray)

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .jsonArray
        .map {
            textRectifier.process("".toPath(), it)
        }.let(::JsonArray)
}
