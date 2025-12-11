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
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
class FormValidatorFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {
    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val validator: FormValidatorProtocol<Any>,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(
        input: Input
    ) = with(input) {
        Output(
            validator = prototypeObject.withScope(FormValidatorSchema::Scope).let {
                when (it.type) {
                    Type.stringMinLength -> StringMinLengthFormValidator(
                        errorMessagesId = it.messageErrorId,
                        length = it.length!!.toInt()
                    )

                    Type.stringMaxLength -> StringMaxLengthFieldFormValidator(
                        errorMessagesId = it.messageErrorId,
                        length = it.length!!.toInt()
                    )

                    Type.stringMinDigitLength -> StringMinDigitLengthFormValidator(
                        errorMessagesId = it.messageErrorId,
                        length = it.length!!.toInt()
                    )

                    Type.stringOnlyDigits -> StringOnlyDigitsFormValidator(
                        errorMessagesId = it.messageErrorId
                    )

                    Type.stringEmail -> StringEmailFormValidator(
                        errorMessagesId = it.messageErrorId
                    )

                    Type.stringNotNull -> StringNotNullFormValidator(
                        errorMessagesId = it.messageErrorId
                    )

                    Type.stringMinValue -> StringMinValueFormValidator(
                        errorMessagesId = it.messageErrorId,
                        minValue = it.value!!.toInt()
                    )

                    Type.stringMaxValue -> StringMaxValueFormValidator(
                        errorMessagesId = it.messageErrorId,
                        maxValue = it.value!!.toInt()
                    )

                    else -> throw DomainException.Default("Validator $prototypeObject can't be resolved")
                }
            } as FormValidatorProtocol<Any>
        )
    }
}
