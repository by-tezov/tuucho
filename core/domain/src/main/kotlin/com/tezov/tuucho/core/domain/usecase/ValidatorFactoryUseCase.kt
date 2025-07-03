package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.ValidatorProtocol
import com.tezov.tuucho.core.domain.schema.ValidatorSchema
import com.tezov.tuucho.core.domain.schema.ValidatorSchema.Companion.length
import com.tezov.tuucho.core.domain.schema.ValidatorSchema.Companion.messageError
import com.tezov.tuucho.core.domain.schema.ValidatorSchema.Companion.type
import com.tezov.tuucho.core.domain.validator.StringMaxLengthValidator
import com.tezov.tuucho.core.domain.validator.StringMinDigitLengthValidator
import com.tezov.tuucho.core.domain.validator.StringMinLengthValidator
import kotlinx.serialization.json.JsonObject

class ValidatorFactoryUseCase {

    fun invoke(prototype: JsonObject): ValidatorProtocol<Any> {

        @Suppress("UNCHECKED_CAST")
        return when (prototype.type) {
            ValidatorSchema.Value.Validator.Type.stringMinLength -> StringMinLengthValidator(
                length = prototype.length.toInt(),
                errorMessages = prototype.messageError
            )

            ValidatorSchema.Value.Validator.Type.stringMaxLength -> StringMaxLengthValidator(
                length = prototype.length.toInt(),
                errorMessages = prototype.messageError
            )

            ValidatorSchema.Value.Validator.Type.stringMinDigitLength -> StringMinDigitLengthValidator(
                length = prototype.length.toInt(),
                errorMessages = prototype.messageError
            )

            else -> throw IllegalStateException("")

        } as ValidatorProtocol<Any>
    }

}