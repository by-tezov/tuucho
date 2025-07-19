package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import kotlinx.serialization.json.JsonObject

object ValidatorSchema {

    const val root = "validator"

    object Key {
        const val type = "type"
        const val idMessageError = "id-message-error"
        const val messageError = "message-error"

        const val length = "length"
        const val value = "value"
    }

    object Value {
        object Type {
            const val stringMinLength = "string-min-length"
            const val stringMaxLength = "string-max-length"
            const val stringMinDigitLength = "string-min-digit-length"
            const val stringOnlyDigits = "string-only-digits"
            const val stringEmail = "string-email"
            const val stringNotNull = "string-not-null"
            const val stringMinValue = "string-min-value"
            const val stringMaxValue = "string-max-value"
        }
    }

    class Scope : OpenSchemaScope<Scope>() {
        override val root = ValidatorSchema.root

        var type by delegate<String?>(Key.type)
        var idMessageError by delegate<String?>(Key.idMessageError)
        var messageError by delegate<JsonObject?>(Key.messageError)

        var length by delegate<String?>(Key.length)
        var value by delegate<String?>(Key.value)
    }

}



