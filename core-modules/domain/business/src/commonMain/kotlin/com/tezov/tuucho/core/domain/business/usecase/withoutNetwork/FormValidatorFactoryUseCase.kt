package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork


import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormValidatorSchema.Value.Type
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase.Output
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringEmailFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMaxLengthFieldFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMaxValueFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinDigitLengthFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinLengthFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinValueFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringNotNullFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringOnlyDigitsFormValidator
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
                        errorMessages = it.messageError!!,
                        length = it.length!!.toInt()
                    )

                    Type.stringMaxLength -> StringMaxLengthFieldFormValidator(
                        errorMessages = it.messageError!!,
                        length = it.length!!.toInt()
                    )

                    Type.stringMinDigitLength -> StringMinDigitLengthFormValidator(
                        errorMessages = it.messageError!!,
                        length = it.length!!.toInt()
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
                        minValue = it.value!!.toInt()
                    )

                    Type.stringMaxValue -> StringMaxValueFormValidator(
                        errorMessages = it.messageError!!,
                        maxValue = it.value!!.toInt()
                    )

                    else -> throw DomainException.Default("Validator $prototypeObject can't be resolved")
                }
            } as FormValidatorProtocol<Any>
        )
    }

}