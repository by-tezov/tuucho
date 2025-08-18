package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationOptionSchema
import kotlinx.serialization.json.JsonObject

data class NavigationOption(
    val single: Boolean,
    val reuse: String?,
    val popUpTo: PopUpTo?,
    val clearStack: Boolean,
) {
    data class PopUpTo(
        val route: NavigationRoute,
        val inclusive: Boolean,
        val greedy: Boolean,
    )

    companion object {
        fun from(optionObject: JsonObject) =
            with(optionObject.withScope(SettingNavigationOptionSchema::Scope)) {
                NavigationOption(
                    single = single ?: false,
                    reuse = reuse?.let {
                        if (it.toBooleanStrictOrNull() == true) {
                            SettingNavigationOptionSchema.Value.Reuse.last
                        } else it
                    },
                    popUpTo = popupTo?.withScope(SettingNavigationOptionSchema.PopUpTo::Scope)
                        ?.let {
                            PopUpTo(
                                route = NavigationRoute.Url(
                                    "", it.url
                                        ?: throw DomainException.Default("url should not be null, fix your json popupUpTo navigation")
                                ),
                                inclusive = it.inclusive ?: false,
                                greedy = it.greedy ?: true
                            )
                        },
                    clearStack = clearStack ?: false
                )
            }
    }

}