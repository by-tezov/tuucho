@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StateSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
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

        class Scope(
            argument: SchemaScopeArgument
        ) : ContentSchema.OpenScope<Scope>(argument) {
            var title by delegate<JsonObject?>(Key.title)
            var placeholder by delegate<JsonObject?>(Key.placeholder)
            var messageError by delegate<JsonArray?>(Key.messageError)
        }
    }

    object State {
        object Key {
            const val initialValue = "initial-value"
        }

        class Scope(
            argument: SchemaScopeArgument
        ) : OpenScope<Scope>(argument)

        open class OpenScope<T : OpenScope<T>>(
            argument: SchemaScopeArgument
        ) : StateSchema.OpenScope<T>(argument) {
            var initialValue by delegate<JsonObject?>(Key.initialValue)
        }
    }
}
