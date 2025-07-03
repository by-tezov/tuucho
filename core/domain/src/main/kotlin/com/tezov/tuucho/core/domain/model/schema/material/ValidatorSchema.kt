package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import kotlinx.serialization.json.JsonObject

object ValidatorSchema {

    const val root = "validator"

    object Key {
        const val type = "type"
        const val length = "length"
        const val idMessageError = "id-message-error"
        const val messageError = "message-error"
    }

    object Value {
        object Type {
            const val stringMinLength = "string-min-length"
            const val stringMaxLength = "string-max-length"
            const val stringMinDigitLength = "string-min-digit-length"
        }
    }

    class Scope : OpenSchemaScope<Scope>() {
        override val root = ValidatorSchema.root

        var type by delegate<String?>(Key.type)
        var length by delegate<String?>(Key.length)
        var idMessageError by delegate<String?>(Key.idMessageError)
        var messageError by delegate<JsonObject?>(Key.messageError)
    }

}



