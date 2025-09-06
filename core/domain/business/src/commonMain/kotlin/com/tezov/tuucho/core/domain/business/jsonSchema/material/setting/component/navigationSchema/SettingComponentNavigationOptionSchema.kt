package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonObject

object ComponentSettingNavigationOptionSchema {

    const val root = "option"

    object Key {
        const val single = "single"
        const val reuse = "reuse"
        const val clearStack = "clear-stack"
        const val popupTo = PopUpTo.root
    }

    object Value {
        object Reuse {
            const val first = "first"
            const val last = "last"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = ComponentSettingNavigationOptionSchema.root

        var single by delegate<Boolean?>(Key.single)
        var reuse by delegate<String?>(Key.reuse)
        var clearStack by delegate<Boolean?>(Key.clearStack)
        var popupTo by delegate<JsonObject?>(Key.popupTo)
    }

    object PopUpTo {
        const val root = "pop-up-to"

        object Key {
            const val url = "url"
            const val inclusive = "inclusive"
            const val greedy = "greedy"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = PopUpTo.root

            var url by delegate<String?>(Key.url)
            var inclusive by delegate<Boolean?>(Key.inclusive)
            var greedy by delegate<Boolean?>(Key.greedy)
        }
    }
}