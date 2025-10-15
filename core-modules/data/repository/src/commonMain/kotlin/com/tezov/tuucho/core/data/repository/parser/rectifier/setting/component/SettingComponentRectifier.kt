package com.tezov.tuucho.core.data.repository.parser.rectifier.setting.component

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentStartWith
import com.tezov.tuucho.core.data.repository.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class SettingComponentRectifier : AbstractRectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        MaterialRectifierModule.Name.Matcher.SETTING
    )

    override val childProcessors: List<AbstractRectifier> by inject(
        MaterialRectifierModule.Name.Processor.SETTING
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement,
    ) = (path.lastSegmentStartWith(TypeSchema.Value.Setting.prefix) && path.parentIsTypeOf(
        element, TypeSchema.Value.component
    )) || super.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ComponentSettingSchema::Scope).apply {
        type = TypeSchema.Value.Setting.component
        val value = this.element.string.requireIsRef()
        id = JsonPrimitive(value)
    }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ComponentSettingSchema::Scope).apply {
        type = TypeSchema.Value.Setting.component
        id ?: run { id = JsonNull }
    }.collect()

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = with(element.find(path).jsonArray) {
        JsonArray(map { process("".toPath(), it) })
    }

}