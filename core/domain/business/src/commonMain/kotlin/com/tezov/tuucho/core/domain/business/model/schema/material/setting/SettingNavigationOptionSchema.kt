package com.tezov.tuucho.core.domain.business.model.schema.material.setting

import com.tezov.tuucho.core.domain.business.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object SettingNavigationOptionSchema {

    const val root = SettingSchema.Root.Key.navigationOption

    object Key {
        const val selector = "selector"
        const val singleTop = "single-top"
        const val clearStack = "clear-stack"
        const val popupTo = "pop-up-to"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SettingSchema.root
        var self by delegate<JsonObject?>(root)

        var selector by delegate<JsonObject?>(Key.selector)
        var singleTop by delegate<Boolean?>(Key.singleTop)
        var clearStack by delegate<Boolean?>(Key.clearStack)
        var popupTo by delegate<JsonObject?>(Key.popupTo)
    }

    object Selector {
        const val root = SettingNavigationOptionSchema.Key.selector

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
            override val root = Selector.root
            var self by delegate<JsonObject?>(root)

            var type by delegate<String?>(Key.type)
            var values by delegate<JsonArray?>(Key.value)
        }
    }

    object PopUpTo {
        const val root = SettingNavigationOptionSchema.Key.popupTo

        object Key {
            const val url = "url"
            const val inclusive = "inclusive"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = PopUpTo.root

            var url by delegate<String?>(Key.url)
            var inclusive by delegate<Boolean?>(Key.inclusive)
        }
    }

}