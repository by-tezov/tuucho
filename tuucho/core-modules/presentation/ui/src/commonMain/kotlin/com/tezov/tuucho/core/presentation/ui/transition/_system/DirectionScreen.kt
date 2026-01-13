@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.transition._system

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonObject

enum class DirectionScreen(
    private val value: String
) {
    Enter(SettingComponentNavigationTransitionSchema.Spec.Value.DirectionScreen.enter),
    Exit(SettingComponentNavigationTransitionSchema.Spec.Value.DirectionScreen.exit);

    companion object {
        fun from(
            specObject: JsonObject
        ) = specObject
            .withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
            .directionScreen
            ?.let { value -> DirectionScreen.entries.firstOrNull { it.value == value } }
            ?: throw UiException.Default("unknown direction screen in spec $specObject")
    }
}
