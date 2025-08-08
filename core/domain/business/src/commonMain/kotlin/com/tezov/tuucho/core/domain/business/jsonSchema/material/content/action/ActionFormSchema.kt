package com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument

object ActionFormSchema {

    object Send {
        object Key {
            const val actionValidated = "action-validated"
        }

        class Scope(argument: SchemaScopeArgument) : ActionSchema.OpenScope<Scope>(argument) {
            var actionValidated by delegate<String?>(Key.actionValidated)
        }
    }


}