package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringEmailFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringMaxLengthFieldFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringMaxValueFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringMinDigitLengthFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringMinLengthFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringMinValueFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringNotNullFormValidator
import com.tezov.tuucho.core.domain.business.Validator.formValidator.StringOnlyDigitsFormValidator
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.option.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.option.FormValidatorSchema.Value.Type
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.FormValidatorFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.FormValidatorFactoryUseCase.Output
import kotlinx.serialization.json.JsonObject

class FormValidatorFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {

    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val validator: FormValidatorProtocol<Any>,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(input: Input) = with(input) {
        Output(
            validator = prototypeObject.withScope(FormValidatorSchema::Scope).let {
                when (it.type) {
                    Type.stringMinLength -> StringMinLengthFormValidator(
                        length = it.length!!.toInt(),
                        errorMessages = it.messageError!!
                    )

                    Type.stringMaxLength -> StringMaxLengthFieldFormValidator(
                        length = it.length!!.toInt(),
                        errorMessages = it.messageError!!
                    )

                    Type.stringMinDigitLength -> StringMinDigitLengthFormValidator(
                        length = it.length!!.toInt(),
                        errorMessages = it.messageError!!
                    )

                    Type.stringOnlyDigits -> StringOnlyDigitsFormValidator(
                        errorMessages = it.messageError!!
                    )

                    Type.stringEmail -> StringEmailFormValidator(
                        errorMessages = it.messageError!!
                    )

                    Type.stringNotNull -> StringNotNullFormValidator(
                        errorMessages = it.messageError!!
                    )

                    Type.stringMinValue -> StringMinValueFormValidator(
                        errorMessages = it.messageError!!,
                        minValue = it.value!!.toInt(),
                    )

                    Type.stringMaxValue -> StringMaxValueFormValidator(
                        errorMessages = it.messageError!!,
                        maxValue = it.value!!.toInt(),
                    )

                    else -> throw DomainException.Default("Validator $prototypeObject can't be resolved")
                }
            } as FormValidatorProtocol<Any>
        )
    }

}