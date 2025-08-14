package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object SettingOptionSelector {
    const val root = "option-selector"

    object Key {
        const val type = "type"
        const val value = "value"
    }

    object Value {
        object Type {
            const val pageBreadCrumb = "page-bread-crumb"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SettingOptionSelector.root
        var self by delegate<JsonObject?>(root)

        var type by delegate<String?>(Key.type)
        var values by delegate<JsonArray?>(Key.value)
    }
}