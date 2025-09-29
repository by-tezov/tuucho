package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray

object ComponentSettingNavigationSelectorSchema {

    const val root = "selector"

    object Key {
        const val type = "type"
        const val value = "value"
        const val values = "values"
    }

    object Value {
        object Type {
            const val pageBreadCrumb = "page-bread-crumb"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = ComponentSettingNavigationSelectorSchema.root

        var type by delegate<String?>(Key.type)
        var values by delegate<JsonArray?>(Key.values)
    }

}