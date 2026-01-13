package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import kotlinx.serialization.json.JsonObject

object SpacerSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.spacer
        }
    }

    object Style {
        object Key {
            const val weight = "weight"
        }

        class Scope(
            argument: SchemaScopeArgument
        ) : StyleSchema.OpenScope<Scope>(argument) {
            var weight by delegate<JsonObject?>(Key.weight)
        }
    }
}
