package com.tezov.tuucho.core.presentation.ui.transition._system

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonObject

enum class DirectionScreen(private val value: String) {

    Enter(SettingNavigationTransitionSchema.Spec.Value.DirectionScreen.enter),
    Exit(SettingNavigationTransitionSchema.Spec.Value.DirectionScreen.exit);

    companion object {
        fun from(specObject: JsonObject) = specObject
            .withScope(SettingNavigationTransitionSchema.Spec::Scope)
            .directionScreen?.let { value -> DirectionScreen.entries.firstOrNull { it.value == value } }
            ?: throw UiException.Default("unknown direction screen in spec $specObject")
    }
}