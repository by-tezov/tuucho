package com.tezov.tuucho.core.domain.model.schema.material._element

import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.model.schema.material.ContentSchema
import com.tezov.tuucho.core.domain.model.schema.material.OptionSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object FieldSchema {

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

    object Option {
        object Key {
            const val validator = "validator"
        }

        class Scope(argument: SchemaScopeArgument) : OptionSchema.OpenScope<Scope>(argument) {

            var validator by delegate<JsonArray?>(Key.validator)

        }
    }

}
