package com.tezov.tuucho.core.presentation.ui.transition._system

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonObject

enum class DirectionNavigation(private val value: String) {

    Forward(SettingComponentNavigationTransitionSchema.Spec.Value.DirectionNavigation.forward),
    Backward(SettingComponentNavigationTransitionSchema.Spec.Value.DirectionNavigation.backward);

    companion object {
        fun from(specObject: JsonObject) = specObject
            .withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
            .directionNavigation?.let { value -> DirectionNavigation.entries.firstOrNull { it.value == value } }
            ?: throw UiException.Default("unknown direction navigation in spec $specObject")
    }
}