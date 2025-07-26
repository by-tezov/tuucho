package com.tezov.tuucho.core.domain.usecase


import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ValidatorSchema
import com.tezov.tuucho.core.domain.model.schema.material.ValidatorSchema.Value.Type
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringEmailValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMaxLengthFieldValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMaxValueValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMinDigitLengthFieldValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMinLengthFieldValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringMinValueValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringNotNullValidator
import com.tezov.tuucho.core.domain.validator.fieldValidator.StringOnlyDigitsValidator
import kotlinx.serialization.json.JsonObject

class ValidatorFactoryUseCase {

    @Suppress("UNCHECKED_CAST")
    fun invoke(prototype: JsonObject) = prototype.withScope(ValidatorSchema::Scope).let {
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

            Type.stringOnlyDigits -> StringOnlyDigitsValidator(
                errorMessages = it.messageError!!
            )

            Type.stringEmail -> StringEmailValidator(
                errorMessages = it.messageError!!
            )

            Type.stringNotNull -> StringNotNullValidator(
                errorMessages = it.messageError!!
            )

            Type.stringMinValue -> StringMinValueValidator(
                errorMessages = it.messageError!!,
                minValue = it.value!!.toInt(),
            )

            Type.stringMaxValue -> StringMaxValueValidator(
                errorMessages = it.messageError!!,
                maxValue = it.value!!.toInt(),
            )

            else -> error("Validator $prototype can't be resolved")
        }
    } as FieldValidatorProtocol<Any>

}