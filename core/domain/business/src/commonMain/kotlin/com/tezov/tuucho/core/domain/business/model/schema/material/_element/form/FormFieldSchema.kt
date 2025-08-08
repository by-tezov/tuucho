package com.tezov.tuucho.core.domain.business.model.schema.material._element.form

import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.model.schema.material.content.ContentSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
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
            const val messageError = "message-error"
        }

        class Scope(argument: SchemaScopeArgument) : ContentSchema.OpenScope<Scope>(argument) {

            var title by delegate<JsonObject?>(Key.title)
            var placeholder by delegate<JsonObject?>(Key.placeholder)
            var messageError by delegate<JsonArray?>(Key.messageError)

        }
    }

}