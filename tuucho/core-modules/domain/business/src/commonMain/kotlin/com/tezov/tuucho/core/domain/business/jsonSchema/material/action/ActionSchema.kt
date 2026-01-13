package com.tezov.tuucho.core.domain.business.jsonSchema.material.action

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

object ActionSchema {
    const val root = "action"

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root

        const val primaries = "primaries"
    }

    object Value {
        object Group {
            const val common = "common"
        }
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<T>(argument) {
        override val root = ActionSchema.root

        var id by delegate<JsonElement>(Key.id)
        var type by delegate<String?>(Key.type)

        var primaries by delegate<JsonArray?>(Key.primaries)
    }
}
