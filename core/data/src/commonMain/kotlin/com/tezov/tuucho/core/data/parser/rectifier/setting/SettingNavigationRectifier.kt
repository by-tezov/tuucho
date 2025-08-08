package com.tezov.tuucho.core.data.parser.rectifier.setting

import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

class SettingNavigationRectifier : Rectifier() {

    override val childProcessors = listOf(
        SettingNavigationDefinitionRectifier()
    )

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
         if (!path.lastSegmentIs(SettingSchema.Root.Key.navigation)) return false
        return element.find(path.parent()).isTypeOf(TypeSchema.Value.setting)
    }
}
