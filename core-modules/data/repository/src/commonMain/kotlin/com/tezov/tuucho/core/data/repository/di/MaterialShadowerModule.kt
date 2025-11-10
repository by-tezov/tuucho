package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.shadower.AbstractShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.ComponentShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.ContentShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.StateShadower
import com.tezov.tuucho.core.data.repository.parser.shadower.TextShadower
import com.tezov.tuucho.core.data.repository.parser.shadower._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.repository.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.core.module.Module
import org.koin.core.qualifier.named

internal object MaterialShadowerModule {
    object Name {
        object Processor {
            val COMPONENT = named("MaterialShadowerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialShadowerModule.Name.Processor.CONTENT")
            val STATE = named("MaterialShadowerModule.Name.Processor.STATE")
            val TEXT = named("MaterialShadowerModule.Name.Processor.TEXT")
        }

        object Matcher {
            val COMPONENT = named("MaterialShadowerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialShadowerModule.Name.Matcher.CONTENT")
            val STATE = named("MaterialShadowerModule.Name.Matcher.STATE")
            val TEXT = named("MaterialShadowerModule.Name.Matcher.TEXT")
        }
    }

    fun invoke() = module(ModuleGroupData.Shadower) {
        single<MaterialShadower> { MaterialShadower() }

        componentModule()
        contentModule()
        stateModule()
        textModule()
    }

    private fun Module.componentModule() {
        single<ComponentShadower> { ComponentShadower() }

        single<List<MatcherShadowerProtocol>>(Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearItemsMatcher()
            )
        }

        single<List<AbstractShadower>>(Name.Processor.COMPONENT) {
            listOf(
                get<ContentShadower>()
            )
        }
    }

    private fun Module.contentModule() {
        single<ContentShadower> { ContentShadower() }

        single<List<MatcherShadowerProtocol>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<AbstractShadower>>(Name.Processor.CONTENT) {
            listOf(
                get<ComponentShadower>(),
                get<TextShadower>()
            )
        }
    }

    private fun Module.stateModule() {
        single<StateShadower> { StateShadower() }

        single<List<MatcherShadowerProtocol>>(Name.Matcher.STATE) {
            emptyList()
        }

        single<List<AbstractShadower>>(Name.Processor.STATE) {
            emptyList()
        }
    }

    private fun Module.textModule() {
        single<TextShadower> { TextShadower() }

        single<List<MatcherShadowerProtocol>>(Name.Matcher.TEXT) {
            emptyList()
        }

        single<List<AbstractShadower>>(Name.Processor.TEXT) {
            emptyList()
        }
    }
}
