package com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component

import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class SettingComponentNavigationRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = ComponentSettingNavigationSchema.root

    override val childProcessors = listOf(
        SettingComponentNavigationDefinitionRectifier(scope)
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(ComponentSettingSchema.Root.Key.navigation)) return false
        return element.find(path.parent()).isTypeOf(TypeSchema.Value.Setting.component)
    }
}
