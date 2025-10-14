package com.tezov.tuucho.core.domain.business.jsonSchema.response

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action.ActionSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object FormSendResponseSchema {

    object Key {
        const val type = TypeResponseSchema.root
        const val allSucceed = "all-succeed"
        const val failureResult = FailureResult.root
        const val action = ActionSchema.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        var type by delegate<String?>(Key.type)
        var allSucceed by delegate<Boolean?>(Key.allSucceed)
        var failureResult by delegate<JsonArray?>(Key.failureResult)
        var action by delegate<JsonObject?>(Key.action)
    }

    object FailureResult {
        const val root = "failure-result"

        object Key {
            const val id = IdSchema.root
            const val reason = "reason"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = FailureResult.root

            var id by delegate<String?>(Key.id)
            var reason by delegate<JsonObject?>(Key.reason)
        }
    }

}



