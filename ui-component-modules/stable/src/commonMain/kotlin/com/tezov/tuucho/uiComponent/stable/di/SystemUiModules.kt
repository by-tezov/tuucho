package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.domain.business.di.Koin

object SystemUiModules {
    fun invoke(): List<Koin> = listOf(
        MaterialRectifierModule.invoke(),
        MaterialAssemblerModule.invoke(),
        ViewModule.invoke(),
    )
}
