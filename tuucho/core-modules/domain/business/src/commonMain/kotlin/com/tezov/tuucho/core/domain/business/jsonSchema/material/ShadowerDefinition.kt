package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema

object Shadower {
    object Contextual {
        fun String.replaceUrlOriginToken(
            value: String
        ) = replace($$"${$${ConfigSchema.MaterialResource.Contextual.Setting.Key.urlOrigin}}", value)

        fun defaultUrl(
            base: String
        ) = "$base-${SettingComponentShadowerSchema.Key.contextual}"
    }
}
