package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import kotlinx.serialization.json.JsonObject

object ButtonSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.button
        }
    }

    object Content {
        object Key {
            const val label = LabelSchema.Component.Value.subset
            const val action = ActionSchema.root
        }
    }
}
