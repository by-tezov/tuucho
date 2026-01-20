package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement

object TimeToLive {
    const val root = "time-to-live"

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val strategy = "strategy"
        const val transientValue = "transient-value"
    }

    object Value {
        object Group {
            const val common = "common"
        }

        object Strategy {
            const val transient = "transient"
            const val singleUse = "single-use"
        }
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        override val root = TimeToLive.root

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var strategy by delegate<String?>(Key.strategy)
        var transientValue by delegate<String?>(Key.transientValue)
    }
}
