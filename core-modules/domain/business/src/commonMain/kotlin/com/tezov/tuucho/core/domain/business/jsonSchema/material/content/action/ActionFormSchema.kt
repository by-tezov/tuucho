package com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray

object ActionFormSchema {

    object Send {
        object Key {
            const val validated = "validated"
            const val denied = "denied"
            const val before = "before"
            const val after = "after"
        }

        class Scope(argument: SchemaScopeArgument) : ActionSchema.OpenScope<Scope>(argument) {
            var validated by delegate<JsonArray?>(Key.validated)
            var denied by delegate<JsonArray?>(Key.denied)
            var before by delegate<JsonArray?>(Key.before)
            var after by delegate<JsonArray?>(Key.after)
        }
    }
}