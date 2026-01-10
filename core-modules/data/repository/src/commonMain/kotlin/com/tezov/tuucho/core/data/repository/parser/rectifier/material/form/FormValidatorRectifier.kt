package com.tezov.tuucho.core.data.repository.parser.rectifier.material.form

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SymbolData
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.hasGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.scope.Scope

sealed class FieldValidatorAssociation {
    object Matcher : FieldValidatorAssociation()
}

class FormValidatorRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = FormValidatorSchema.root
    override val matchers: List<RectifierMatcherProtocol> by lazy {
        scope.getAllAssociated(FieldValidatorAssociation.Matcher::class)
    }

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject(
        path = ROOT_PATH,
        element = element
            .find(path)
            .withScope(FormValidatorSchema::Scope)
            .apply {
                type = this.element.string
            }.collect()
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        add(element.find(path))
    }.let(::JsonArray)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        val jsonArray = element.find(path).jsonArray
        if (!jsonArray.any { it is JsonPrimitive }) return null
        return JsonArray(jsonArray.map {
            if (it is JsonPrimitive) {
                it
                    .withScope(FormValidatorSchema::Scope)
                    .apply {
                        type = this.element.string
                    }.collect()
            } else {
                it
            }
        })
    }

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = JsonArray(element.find(path).jsonArray.map { jsonObject ->
        val scope = jsonObject.withScope(FormValidatorSchema::Scope)
        var idMessageErrorRectified: String? = null
        with(scope) {
            if (messageErrorId?.startsWith(SymbolData.ID_REF_INDICATOR) == true) {
                idMessageErrorRectified = messageErrorId
                    ?.removePrefix(SymbolData.ID_REF_INDICATOR)
            }
            idMessageErrorRectified = (idMessageErrorRectified ?: scope.messageErrorId)
                ?.takeIf { !it.hasGroup() }
                ?.addGroup(TextSchema.Value.Group.common)
        }
        idMessageErrorRectified?.let {
            scope.messageErrorId = idMessageErrorRectified
        }
        scope.collect()
    })
}
