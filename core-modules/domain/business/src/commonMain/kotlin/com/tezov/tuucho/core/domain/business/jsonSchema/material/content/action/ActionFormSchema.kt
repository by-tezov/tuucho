package com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument

object ActionFormSchema {

    object Send {
        object Key {
            const val actionValidated = "action-validated"
            const val actionDenied = "action-denied"
        }

        class Scope(argument: SchemaScopeArgument) : ActionSchema.OpenScope<Scope>(argument) {
            var actionValidated by delegate<String?>(Key.actionValidated)
            var actionDenied by delegate<String?>(Key.actionDenied)
        }
    }


}