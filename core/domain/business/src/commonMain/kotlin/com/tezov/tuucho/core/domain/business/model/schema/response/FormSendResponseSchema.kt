package com.tezov.tuucho.core.domain.business.model.schema.response

import com.tezov.tuucho.core.domain.business.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object FormSendResponseSchema {

    object Key {
        const val isAllSuccess = "isAllSuccess"
        const val results = Result.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        var isAllSuccess by delegate<Boolean?>(Key.isAllSuccess)
        var results by delegate<JsonArray?>(Key.results)
    }

    object Result {
        const val root = "results"

        object Key {
            const val id = IdSchema.root
            const val failureReason = "failure-reason"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = Result.root

            var id by delegate<String?>(Key.id)
            var failureReason by delegate<JsonObject?>(Key.failureReason)
        }
    }

}



