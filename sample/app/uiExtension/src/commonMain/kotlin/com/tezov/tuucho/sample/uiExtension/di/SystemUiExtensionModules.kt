package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business.di.KoinMass

object SystemUiExtensionModules {
    fun invoke(): List<KoinMass> = listOf(
        MaterialRectifierModule.invoke(),
        ViewModule.invoke(),
        ActionProcessorModule.invoke(),
    )
}
