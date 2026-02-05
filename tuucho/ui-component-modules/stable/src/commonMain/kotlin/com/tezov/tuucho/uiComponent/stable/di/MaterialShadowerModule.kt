package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.shadower.ComponentShadower
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.uiComponent.stable.data.parser.shadower.layout.linear.ContentLayoutLinearItemsMatcher
import org.koin.core.module.Module
import org.koin.plugin.module.dsl.factory

internal object MaterialShadowerModule {
    fun invoke() = module(ModuleContextData.Shadower) {
        componentAssociation()
    }

    private fun Module.componentAssociation() {
        factory<ContentLayoutLinearItemsMatcher>() associate ComponentShadower.Association.Matcher::class
    }
}
