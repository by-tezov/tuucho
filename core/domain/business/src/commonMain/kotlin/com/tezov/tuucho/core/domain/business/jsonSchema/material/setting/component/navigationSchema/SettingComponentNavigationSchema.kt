package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object ComponentSettingNavigationSchema {

    const val root = "navigation"

    object Key {
        const val extra = Extra.root
        const val definition = Definition.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = ComponentSettingSchema.root

        var extra by delegate<JsonObject?>(Key.extra)
        var definition by delegate<JsonArray?>(Key.definition)
    }

    object Extra {
        const val root = "extra"

        object Key {
            const val isBackgroundSolid = "is-background-solid"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = ComponentSettingSchema.root

            var isBackgroundSolid by delegate<Boolean?>(Key.isBackgroundSolid)
        }
    }

    object Definition {
        const val root = "definition"

        object Key {
            const val selector = ComponentSettingNavigationSelectorSchema.root
            const val option = ComponentSettingNavigationOptionSchema.root
            const val transition = SettingComponentNavigationTransitionSchema.root
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = ComponentSettingSchema.root

            var selector by delegate<JsonObject?>(Key.selector)
            var option by delegate<JsonObject?>(Key.option)
            var transition by delegate<JsonObject?>(Key.transition)
        }
    }

}