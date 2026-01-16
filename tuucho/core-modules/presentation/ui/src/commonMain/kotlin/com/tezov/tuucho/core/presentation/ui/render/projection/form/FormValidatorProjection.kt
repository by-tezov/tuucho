package com.tezov.tuucho.core.presentation.ui.render.projection.form

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasResolveStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

interface FormValidatorProjectionProtocol :
    ProjectionProcessorProtocol,
    HasResolveStatusProtocol {
    val validators: List<FormValidatorProtocol<String>>

    fun updateValidity(
        value: String?
    )

    fun isValid(): Boolean?
}

private class FormValidatorProjection(
    override val key: String,
) : FormValidatorProjectionProtocol,
    TuuchoKoinComponent {
    private val useCaseExecutor by inject<UseCaseExecutorProtocol>()
    private val fieldValidatorFactory by inject<FormValidatorFactoryUseCase>()

    override var hasBeenResolved: Boolean? = null
        private set

    override var validators: List<FormValidatorProtocol<String>> = emptyList()
        private set

    override fun updateValidity(
        value: String?
    ) {
        validators.forEach { it.updateValidity(value) }
    }

    override fun isValid(): Boolean = validators.all { it.isValid }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        val jsonArray = jsonElement as? JsonArray ?: run {
            validators = emptyList()
            return
        }
        validators = jsonArray.mapNotNull { validatorObject ->
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
        }
        hasBeenResolved = true
    }
}

fun createFormValidatorProjection(
    key: String
): FormValidatorProjectionProtocol = FormValidatorProjection(
    key = key,
)

fun TypeProjectorProtocols.validators(
    key: String,
): FormValidatorProjectionProtocol = createFormValidatorProjection(key)
