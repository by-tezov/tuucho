package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business._system.string
import com.tezov.tuucho.core.domain.business._system.toPath

import com.tezov.tuucho.core.domain.business.model.schema._system.SymbolData
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema.hasGroup
import com.tezov.tuucho.core.domain.business.model.schema.material.TextSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.ValidatorSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class ValidatorRectifier : Rectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        Name.Matcher.VALIDATOR
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.VALIDATOR
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject("".toPath(), element.find(path)
        .withScope(ValidatorSchema::Scope).apply {
            type = this.element.string
        }
        .collect())

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = mutableListOf<JsonElement>().apply {
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
                it.withScope(ValidatorSchema::Scope).apply {
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
        var valueRectified: String? = null
        return element.withScope(ValidatorSchema::Scope)
            .takeIf { scope ->
                if(scope.idMessageError?.startsWith(SymbolData.ID_REF_INDICATOR) == true) {
                    valueRectified = scope.idMessageError?.removePrefix(SymbolData.ID_REF_INDICATOR)
                }
                valueRectified = (valueRectified ?: scope.idMessageError)
                    ?.takeIf { !it.hasGroup() }
                    ?.addGroup(TextSchema.Value.Group.common)
                valueRectified != null
            }
            ?.apply {
                idMessageError = valueRectified
            }?.collect()
    }


}