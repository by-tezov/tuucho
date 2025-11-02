package com.tezov.tuucho.core.domain.business.jsonSchema.material.action

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray

object ActionFormSchema {
    object Send {
        object Key {
            const val validated = "validated"
            const val denied = "denied"
        }

        class Scope(
            argument: SchemaScopeArgument
        ) : OpenSchemaScope<Scope>(argument) {
            override val root = ActionSchema.root
            var validated by delegate<JsonArray?>(Key.validated)
            var denied by delegate<JsonArray?>(Key.denied)
        }
    }
}
