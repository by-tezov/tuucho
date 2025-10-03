package com.tezov.tuucho.core.data.repository.parser.rectifier.actions

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule
import com.tezov.tuucho.core.data.repository.parser.rectifier.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SymbolData
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action.ActionSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class ActionRectifier : AbstractRectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        MaterialRectifierModule.Name.Matcher.ACTION
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ActionSchema::Scope).apply {
        type = TypeSchema.Value.action
        val value = this.element.string
        if (value.startsWith(SymbolData.ID_REF_INDICATOR)) {
            id = JsonPrimitive(value)
        } else {
            id = JsonNull
            primary = listOf(this.element).let(::JsonArray)
        }
    }.collect()

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ActionSchema::Scope).apply {
        type = TypeSchema.Value.action
        id = JsonNull
        primary = this.element.jsonArray
    }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ActionSchema::Scope).apply {
        type = TypeSchema.Value.action
        id ?: run { id = JsonNull }
        val ignoreKeys = listOf(IdSchema.root, TypeSchema.root)
        keys()
            .asSequence()
            .filter { !ignoreKeys.contains(it) }
            .forEach { toArray(it) }
    }.collect()

    private fun ActionSchema.Scope.toArray(key: String) {
        get(key)?.takeIf {
            it is JsonPrimitive
        }?.let {
            set(key, listOf(it).let(::JsonArray))
        }
    }

}