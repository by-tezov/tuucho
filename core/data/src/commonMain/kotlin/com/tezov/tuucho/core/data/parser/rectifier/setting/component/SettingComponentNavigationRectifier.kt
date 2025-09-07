package com.tezov.tuucho.core.data.parser.rectifier.setting.component

import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

class SettingComponentNavigationRectifier : AbstractRectifier() {

    override val childProcessors = listOf(
        SettingComponentNavigationDefinitionRectifier()
    )

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(ComponentSettingSchema.Root.Key.navigation)) return false
        return element.find(path.parent()).isTypeOf(TypeSchema.Value.Setting.component)
    }
}
