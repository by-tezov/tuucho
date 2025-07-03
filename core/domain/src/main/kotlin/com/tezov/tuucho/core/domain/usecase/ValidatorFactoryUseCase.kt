package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.ValidatorSchema
import com.tezov.tuucho.core.domain.model.schema.material.ValidatorSchema.Value.Type
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMaxLengthFieldValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMinDigitLengthFieldValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMinLengthFieldValidator
import kotlinx.serialization.json.JsonObject

class ValidatorFactoryUseCase {

    @Suppress("UNCHECKED_CAST")
    fun invoke(prototype: JsonObject) = prototype.schema().withScope(ValidatorSchema::Scope).let {
        when (it.type) {
            Type.stringMinLength -> StringMinLengthFieldValidator(
                length = it.length!!.toInt(),
                errorMessages = it.messageError!!
            )

            Type.stringMaxLength -> StringMaxLengthFieldValidator(
                length = it.length!!.toInt(),
                errorMessages = it.messageError!!
            )

            Type.stringMinDigitLength -> StringMinDigitLengthFieldValidator(
                length = it.length!!.toInt(),
                errorMessages = it.messageError!!
            )

            else -> error("Validator $prototype can't be resolved")
        }
    } as FieldValidatorProtocol<Any>

}