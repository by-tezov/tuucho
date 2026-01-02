@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.response.form

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class FormFailureReasonRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = FormSendSchema.Key.failureResults
    private val textRectifier: TextRectifier by inject()

    private val matcher = FormFailureReasonRectifierMatcher()

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
            textRectifier.process(ROOT_PATH, it)
        }.let(::JsonArray)
}
