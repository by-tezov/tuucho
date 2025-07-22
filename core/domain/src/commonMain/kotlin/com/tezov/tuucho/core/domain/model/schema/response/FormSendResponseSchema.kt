package com.tezov.tuucho.core.domain.model.schema.response

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object FormSendResponseSchema {

    object Root {
        object Key {
            const val isAllSuccess = "isAllSuccess"
            const val results = "results"
        }

        class Scope : OpenSchemaScope<Scope>() {
            var isAllSuccess by delegate<Boolean?>(Key.isAllSuccess)
            var results by delegate<JsonArray?>(Key.results)
        }
    }

    object Result {
        object Key {
            const val id = IdSchema.root
            const val failureReason = "failure-reason"
        }

        class Scope : OpenSchemaScope<Scope>() {
            var id by delegate<JsonObject?>(Key.id)
            var failureReason by delegate<JsonObject?>(Key.failureReason)
        }
    }

    object ActionParams {
        object Key {
            const val actionValidated = "action-validated"
        }

        class Scope : OpenSchemaScope<Scope>() {
            var actionValidated by delegate<String?>(Key.actionValidated)
        }
    }
}



