package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.FormValidatorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createFormValidatorProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface ValidatorProjectableProtocols : ProjectableProtocol {
    fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String,
    ): T
}

@TuuchoUiDsl
class ValidatorProjectable : ValidatorProjectableProtocols {
    private val projections = mutableMapOf<String, ProjectionProtocol>()

    override val keys get() = projections.keys.toSet()

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String,
    ) = (when (klass) {
        FormValidatorProjectionProtocol::class -> createFormValidatorProjection(key)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocols.validator(
    block: ValidatorProjectableProtocols.() -> Unit
): ValidatorProjectableProtocols = ValidatorProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> ValidatorProjectableProtocols.projection(
    key: String,
) = newProjection(klass = T::class, key = key)
