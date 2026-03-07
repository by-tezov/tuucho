package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
class FormValidatorFactoryUseCase(
    private val factories: List<FormValidatorProtocol.Factory>
) : UseCaseProtocol.Sync<Input, Output> {
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
            validator = prototypeObject.withScope(FormValidatorSchema::Scope).let { scope ->
                factories.firstOrNull { it.type == scope.type }?.create(
                    errorMessagesId = scope.messageErrorId,
                    prototypeObject = prototypeObject
                ) ?: throw DomainException.Default("Validator $prototypeObject can't be resolved")
            } as FormValidatorProtocol<Any>
        )
    }
}
