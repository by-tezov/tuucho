package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.ValidatorSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.ValidatorSchema.Value.Type
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase.Output
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringEmailValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringMaxLengthFieldValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringMaxValueValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringMinDigitLengthFieldValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringMinLengthFieldValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringMinValueValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringNotNullValidator
import com.tezov.tuucho.core.domain.business.validator.fieldValidator.StringOnlyDigitsValidator
import kotlinx.serialization.json.JsonObject

class ValidatorFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {

    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val validator: FieldValidatorProtocol<Any>,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(input: Input) = with(input) {
        Output(
            validator = prototypeObject.withScope(ValidatorSchema::Scope).let {
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

                    else -> error("Validator ${prototypeObject} can't be resolved")
                }
            } as FieldValidatorProtocol<Any>
        )
    }

}