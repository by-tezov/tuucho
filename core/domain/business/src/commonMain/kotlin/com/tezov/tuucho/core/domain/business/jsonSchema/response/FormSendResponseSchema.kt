package com.tezov.tuucho.core.domain.business.jsonSchema.response

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object FormSendResponseSchema {

    object Key {
        const val type = "type"
        const val content = "content"
    }

    object Value {
        object Type {
            const val allSucceed = "all-succeed"
            const val failureResult = FailureResult.root
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        var type by delegate<String?>(Key.type)
        var content by delegate<JsonElement?>(Key.content)
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



