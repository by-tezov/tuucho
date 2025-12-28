package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.FormStateProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createFormStateProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface FormFieldProjectableProtocols : ProjectableProtocol {
    fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String
    ): T
}

@TuuchoUiDsl
class FormFieldProjectable : FormFieldProjectableProtocols {
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
        key: String
    ) = (when (klass) {
        FormStateProjectionProtocol::class -> createFormStateProjection(key)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocols.field(
    block: FormFieldProjectableProtocols.() -> Unit
): FormFieldProjectableProtocols = FormFieldProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> FormFieldProjectableProtocols.projection(
    key: String,
) = newProjection(klass = T::class, key = key)
