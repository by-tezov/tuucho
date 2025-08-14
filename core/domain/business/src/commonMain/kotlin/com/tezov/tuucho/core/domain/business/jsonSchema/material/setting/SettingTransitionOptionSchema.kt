package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonObject

object SettingTransitionOptionSchema {

    const val root = "transition-option"

    object Key {
        const val selector = SettingOptionSelector.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SettingSchema.root
        var self by delegate<JsonObject?>(root)

        var selector by delegate<JsonObject?>(Key.selector)
    }


}