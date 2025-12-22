// package com.tezov.tuucho.core.presentation.ui.render.projectable
//
// import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
// import com.tezov.tuucho.core.presentation.ui.exception.UiException
// import com.tezov.tuucho.core.presentation.ui.render.projection.FormValidatorProjection
// import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
// import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
// import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
// import kotlinx.serialization.json.JsonElement
// import kotlin.reflect.KClass
//
// @TuuchoUiDsl
// class ValidatorProjectable : ProjectableProtocol {
//
//    private val projections = mutableMapOf<String, ProjectionProtocol>()
//
//    override val keys get() = projections.keys.toSet()
//
//    override suspend fun process(
//        jsonElement: JsonElement?,
//        key: String
//    ) {
//        projections[key]?.process(jsonElement)
//    }
//
//    fun <T : ProjectionProtocol> newProjection(
//        klass: KClass<out T>,
//        key: String,
//    ) =
//        @Suppress("UNCHECKED_CAST")
//        (when (klass) {
//            FormValidatorProjection::class -> FormValidatorProjection(key)
//            else -> throw UiException.Default("not implemented")
//        } as T).also { projections[it.key] = it }
// }
//
// fun TypeProjectorProtocol.validator(
//    block: ValidatorProjectable.() -> Unit
// ): ValidatorProjectable = ValidatorProjectable().also {
//    add(it)
//    it.block()
// }
//
// inline fun <reified T : ProjectionProtocol> ValidatorProjectable.projection(
//    key: String,
// ) = newProjection(klass = T::class, key = key)
