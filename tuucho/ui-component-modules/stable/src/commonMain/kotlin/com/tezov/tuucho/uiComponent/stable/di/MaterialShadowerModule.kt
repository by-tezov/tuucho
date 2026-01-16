package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.shadower.ComponentShadower
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.uiComponent.stable.data.parser.shadower.layout.linear.ContentLayoutLinearItemsMatcher
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal object MaterialShadowerModule {
    fun invoke() = module(ModuleContextData.Shadower) {
        componentAssociation()
    }

    private fun Module.componentAssociation() {
        factoryOf(::ContentLayoutLinearItemsMatcher) associate ComponentShadower.Association.Matcher::class
    }
}
