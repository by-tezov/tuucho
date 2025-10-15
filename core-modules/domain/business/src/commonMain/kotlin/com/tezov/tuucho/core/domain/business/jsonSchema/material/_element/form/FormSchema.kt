package com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MessageSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.OptionSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object FormSchema {

    object Component {
        object Value {
            const val subset = "form-"
        }

    }

    object Option {
        object Key {
            const val validator = FormValidatorSchema.root
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

        open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
            OptionSchema.OpenScope<T>(argument) {

            var validator by delegate<JsonArray?>(Key.validator)

        }
    }

    object Message {

        object Key {
            const val messageErrorExtra = "message-error-extra"
        }

        object Value {
            object Subset {
                const val updateErrorState = "update-error-state"
            }
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

        open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
            MessageSchema.OpenScope<T>(argument) {

            var messageErrorExtra by delegate<JsonObject?>(Key.messageErrorExtra)

        }

    }

}