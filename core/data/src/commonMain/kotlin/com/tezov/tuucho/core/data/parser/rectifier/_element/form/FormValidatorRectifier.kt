package com.tezov.tuucho.core.data.parser.rectifier._element.form

import com.tezov.tuucho.core.data.di.MaterialRectifierModule
import com.tezov.tuucho.core.data.parser.rectifier.AbstractRectifier
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SymbolData
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.hasGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class FormValidatorRectifier : AbstractRectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        MaterialRectifierModule.Name.Matcher.FIELD_VALIDATOR
    )

    override val childProcessors: List<AbstractRectifier> by inject(
        MaterialRectifierModule.Name.Processor.FIELD_VALIDATOR
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject(
        "".toPath(), element.find(path)
            .withScope(FormValidatorSchema::Scope).apply {
                type = this.element.string
            }
            .collect())

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        val jsonArray = element.find(path).jsonArray
        if (!jsonArray.any { it is JsonPrimitive }) return null
        return JsonArray(jsonArray.map {
            if (it is JsonPrimitive) {
                it.withScope(FormValidatorSchema::Scope).apply {
                    type = this.element.string
                }.collect()
            } else it
        })
    }

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).jsonArray.map {
        afterAlterObject("".toPath(), it) ?: it
    }.let(::JsonArray)

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        val scope = element.withScope(FormValidatorSchema::Scope)
        var idMessageErrorRectified: String? = null
        with(scope) {
            if (idMessageError?.startsWith(SymbolData.ID_REF_INDICATOR) == true) {
                idMessageErrorRectified = idMessageError
                    ?.removePrefix(SymbolData.ID_REF_INDICATOR)
            }
            idMessageErrorRectified = (idMessageErrorRectified ?: scope.idMessageError)
                ?.takeIf { !it.hasGroup() }
                ?.addGroup(TextSchema.Value.Group.common)
        }
        idMessageErrorRectified?.let {
            scope.idMessageError = idMessageErrorRectified
        }
        return scope.collectChangedOrNull()
    }


}