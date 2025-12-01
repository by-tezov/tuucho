package com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component

import com.tezov.tuucho.core.data.repository.di.RectifierModule.Material.Name
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentStartWith
import com.tezov.tuucho.core.data.repository.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class SettingComponentRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = ComponentSettingSchema.root

    override val childProcessors: List<AbstractRectifier> by inject(
        Name.Processor.SETTING
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = (path.lastSegmentStartWith(TypeSchema.Value.Setting.prefix) &&
        path.parentIsTypeOf(
            element,
            TypeSchema.Value.component
        )) ||
        super.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ComponentSettingSchema::Scope)
        .apply {
            type = TypeSchema.Value.Setting.component
            val value = this.element.string.requireIsRef()
            id = JsonPrimitive(value)
        }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ComponentSettingSchema::Scope)
        .apply {
            type = TypeSchema.Value.Setting.component
            id ?: run { id = JsonNull }
        }.collect()

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = with(element.find(path).jsonArray) {
        JsonArray(map { process(ROOT_PATH, it) })
    }
}
