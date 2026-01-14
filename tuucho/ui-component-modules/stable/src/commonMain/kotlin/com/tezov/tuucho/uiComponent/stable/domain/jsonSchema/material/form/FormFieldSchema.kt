package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.form

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StateSchema
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.SubsetSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object FormFieldSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.field
        }
    }

    object Content {
        object Key {
            const val title = "title"
            const val placeholder = "placeholder"
            const val messageErrors = "message-errors"
        }
    }

    object State {
        object Key {
            const val initialValue = "initial-value"
        }
    }
}
