package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass

object SystemUiModules {
    fun invoke(): List<KoinMass> = listOf(
        MaterialRectifierModule.invoke(),
        MaterialAssemblerModule.invoke(),
        MaterialShadowerModule.invoke(),
        ViewModule.invoke(),
    )
}
