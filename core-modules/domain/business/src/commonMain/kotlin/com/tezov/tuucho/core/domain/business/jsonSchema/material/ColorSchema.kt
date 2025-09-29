package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement

object ColorSchema {

    const val root = TypeSchema.Value.color

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val default = "default"
    }

    object Value {
        object Group {
            const val common = "common"
            const val palette = "palette"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = ColorSchema.root

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var default by delegate<String?>(Key.default)

    }

}



