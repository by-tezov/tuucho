package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.model.schema.material.SettingSchema

class NavigationOption(
    val singleTop: Boolean? = null,
    val popUpTo: PopUpTo? = null,
    val clearStack: Boolean? = null,
) {
    data class PopUpTo(val route: NavigationRoute, val inclusive: Boolean)

    companion object {
        fun from(settingScope: SettingSchema.Root.Scope): NavigationOption {

            //TODO: parse settingScope to create the correct NavigationOption

            return NavigationOption(
                singleTop = false,
                popUpTo = null,
                clearStack = null
            )
        }
    }

}