package com.tezov.tuucho.ui_component.stable.di

import com.tezov.tuucho.core.domain.business.di.Koin

object SystemUiModules {
    fun invoke(): List<Koin> = listOf(
        MaterialRectifierModule.invoke(),
        ViewModule.invoke(),
    )
}
