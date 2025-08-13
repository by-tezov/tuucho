package com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.ContentSchema
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