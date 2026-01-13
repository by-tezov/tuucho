package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.shadower.ComponentShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.ContentShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.StateShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.TextShadower
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf

internal object MaterialShadowerModule {
    fun invoke() = module(ModuleGroupData.Shadower) {
        shadowers()
        componentAssociation()
        contentAssociation()
    }

    private fun Module.shadowers() {
        singleOf(::MaterialShadower)
        singleOf(::ComponentShadower)
        singleOf(::ContentShadower)
        singleOf(::StateShadower)
        singleOf(::TextShadower)
    }

    private fun Module.componentAssociation() {
        factoryOf(::ContentShadower) associate ComponentShadower.Association.Processor::class
    }

    private fun Module.contentAssociation() {
        associate<ContentShadower.Association.Processor> {
            declaration<ComponentShadower>()
            declaration<TextShadower>()
        }
    }
}
