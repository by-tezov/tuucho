package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.shadower.ComponentShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.ContentShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.OptionShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.StateShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.StyleShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.TextShadower
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.Associate.declaration
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.Module
import org.koin.plugin.module.dsl.single

internal object MaterialShadowerModule {
    fun invoke() = module(ModuleContextData.Shadower) {
        shadowers()
        componentAssociation()
        contentAssociation()
    }

    private fun Module.shadowers() {
        single<MaterialShadower>()
        single<ComponentShadower>()
        single<ContentShadower>()
        single<OptionShadower>()
        single<StyleShadower>()
        single<StateShadower>()
        single<TextShadower>()
    }

    private fun Module.componentAssociation() {
        declaration<ContentShadower>() associate ComponentShadower.Association.Processor::class
    }

    private fun Module.contentAssociation() {
        associate<ContentShadower.Association.Processor> {
            declaration<ComponentShadower>()
            declaration<TextShadower>()
        }
    }
}
