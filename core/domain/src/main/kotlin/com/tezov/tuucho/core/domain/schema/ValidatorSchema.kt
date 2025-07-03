package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

interface ValidatorSchema {

    object Key {
        const val validator = "validator"

        object Validator {
            const val type = "type"
            const val length = "length"
            const val idMessageError = "id-message-error"
            const val messageError = "message-error"
        }
    }

    object Value {

        object Validator {
            object Type {
                const val stringMinLength = "string-min-length"
                const val stringMaxLength = "string-max-length"
                const val stringMinDigitLength = "string-min-digit-length"
            }
        }
    }

    companion object {

        val JsonElement.validatorArray get() = this.jsonObject[Key.validator]!!.jsonArray
        val JsonElement.validatorArrayOrNull get() = (this as? JsonObject)?.get(Key.validator) as? JsonArray

        val JsonElement.type get() = this.jsonObject[Key.Validator.type].string
        val JsonElement.typeOrNull get() =(this as? JsonObject)?.get(Key.Validator.type).stringOrNull

        val JsonElement.length get() = this.jsonObject[Key.Validator.length].string
        val JsonElement.lengthOrNull get() =(this as? JsonObject)?.get(Key.Validator.length).stringOrNull

        val JsonElement.idMessageError get() = this.jsonObject[Key.Validator.idMessageError].string
        val JsonElement.idMessageErrorOrNull get() =(this as? JsonObject)?.get(Key.Validator.idMessageError).stringOrNull

        val JsonElement.messageError get() = this.jsonObject[Key.Validator.messageError]!!.jsonObject
        val JsonElement.messageErrorOrNull get() =(this as? JsonObject)?.get(Key.Validator.messageError) as? JsonObject

    }

}



