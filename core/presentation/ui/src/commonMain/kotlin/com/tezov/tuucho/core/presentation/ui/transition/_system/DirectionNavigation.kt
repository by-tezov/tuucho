package com.tezov.tuucho.core.presentation.ui.transition._system

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonObject

enum class DirectionNavigation(private val value: String) {

    Forward(SettingNavigationTransitionSchema.Spec.Value.DirectionNavigation.forward),
    Backward(SettingNavigationTransitionSchema.Spec.Value.DirectionNavigation.backward);

    companion object {
        fun from(specObject: JsonObject) = specObject
            .withScope(SettingNavigationTransitionSchema.Spec::Scope)
            .directionNavigation?.let { value -> DirectionNavigation.entries.firstOrNull { it.value == value } }
            ?: throw UiException.Default("unknown direction navigation in spec $specObject")
    }
}