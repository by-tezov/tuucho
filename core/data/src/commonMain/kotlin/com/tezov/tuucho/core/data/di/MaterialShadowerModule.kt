package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser.shadower.ComponentShadower
import com.tezov.tuucho.core.data.parser.shadower.ContentShadower
import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.parser.shadower.Shadower
import com.tezov.tuucho.core.data.parser.shadower.TextShadower
import com.tezov.tuucho.core.data.parser.shadower._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.parser.shadower._system.MatcherShadowerProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialShadowerModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialShadowerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialShadowerModule.Name.Processor.CONTENT")
            val TEXT = named("MaterialShadowerModule.Name.Processor.TEXT")
        }

        object Matcher {
            val COMPONENT = named("MaterialShadowerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialShadowerModule.Name.Matcher.CONTENT")
            val TEXT = named("MaterialShadowerModule.Name.Matcher.TEXT")
        }
    }

    internal operator fun invoke() = module {
        single<MaterialShadower> { MaterialShadower() }
        componentModule()
        contentModule()
        textModule()
    }

    private fun Module.componentModule() {
        single<ComponentShadower> { ComponentShadower() }

        single<List<MatcherShadowerProtocol>>(Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearItemsMatcher()
            )
        }

        single<List<Shadower>>(Name.Processor.COMPONENT) {
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

        single<List<Shadower>>(Name.Processor.CONTENT) {
            listOf(
                get<ComponentShadower>(),
                get<TextShadower>()
            )
        }
    }

    private fun Module.textModule() {
        single<TextShadower> { TextShadower() }

        single<List<MatcherShadowerProtocol>>(Name.Matcher.TEXT) {
            emptyList()
        }

        single<List<Shadower>>(Name.Processor.TEXT) {
            emptyList()
        }
    }

}


