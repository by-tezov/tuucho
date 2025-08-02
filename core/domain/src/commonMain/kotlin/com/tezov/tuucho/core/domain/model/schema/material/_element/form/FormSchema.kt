package com.tezov.tuucho.core.domain.model.schema.material._element.form

import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.model.schema.material.MessageSchema
import com.tezov.tuucho.core.domain.model.schema.material.OptionSchema
import com.tezov.tuucho.core.domain.model.schema.material.StateSchema
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
            const val validator = "validator"
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

        open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
            OptionSchema.OpenScope<T>(argument) {

            var validator by delegate<JsonArray?>(Key.validator)

        }
    }

    object State {
        object Key {
            const val initialValue = "initial-value"
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

        open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
            StateSchema.OpenScope<T>(argument) {

            var initialValue by delegate<JsonObject?>(Key.initialValue)

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