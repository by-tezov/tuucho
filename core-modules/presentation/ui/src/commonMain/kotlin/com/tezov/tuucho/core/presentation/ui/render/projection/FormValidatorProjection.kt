package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

interface FormValidatorProjectionProtocol : ProjectionProtocol {
    val validators: List<FormValidatorProtocol<String>>?

    fun updateValidity(
        value: String?
    )

    fun isValid(): Boolean?
}

class FormValidatorProjection(
    override val key: String,
) : FormValidatorProjectionProtocol,
    TuuchoKoinComponent {
    private val useCaseExecutor by inject<UseCaseExecutorProtocol>()
    private val fieldValidatorFactory by inject<FormValidatorFactoryUseCase>()

    override var validators: List<FormValidatorProtocol<String>>? = null
        private set

    override fun updateValidity(
        value: String?
    ) {
        validators?.forEach { it.updateValidity(value) }
    }

    override fun isValid(): Boolean? = validators?.all { it.isValid }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        validators = (jsonElement as? JsonArray)
            ?.mapNotNull { validatorObject ->
                val validatorPrototype = validatorObject as? JsonObject
                validatorPrototype?.let {
                    @Suppress("UNCHECKED_CAST")
                    useCaseExecutor
                        .await(
                            useCase = fieldValidatorFactory,
                            input = FormValidatorFactoryUseCase.Input(
                                prototypeObject = validatorPrototype
                            ),
                        )?.validator as? FormValidatorProtocol<String>
                }
            }?.takeIf { it.isNotEmpty() }
    }
}

fun createFormValidatorProjection(
    key: String
): FormValidatorProjectionProtocol = FormValidatorProjection(
    key = key,
)
