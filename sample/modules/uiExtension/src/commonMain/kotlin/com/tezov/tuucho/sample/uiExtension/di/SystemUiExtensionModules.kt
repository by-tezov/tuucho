package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass

object SystemUiExtensionModules {
    fun invoke(): List<KoinMass> = listOf(
        MaterialRectifierModule.invoke(),
        ViewModule.invoke(),
        ActionProcessorModule.invoke(),
    )
}
