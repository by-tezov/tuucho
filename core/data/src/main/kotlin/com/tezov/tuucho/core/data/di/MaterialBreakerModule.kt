package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser.breaker.Breaker
import com.tezov.tuucho.core.data.parser.breaker.ColorBreaker
import com.tezov.tuucho.core.data.parser.breaker.ComponentBreaker
import com.tezov.tuucho.core.data.parser.breaker.ContentBreaker
import com.tezov.tuucho.core.data.parser.breaker.DimensionBreaker
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.breaker.StyleBreaker
import com.tezov.tuucho.core.data.parser.breaker.TextBreaker
import com.tezov.tuucho.core.data.parser.breaker._element.layout.linear.ContentLayoutLinearMatcher
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialBreakerModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialBreakerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialBreakerModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialBreakerModule.Name.Processor.STYLE")
            val TEXT = named("MaterialBreakerModule.Name.Processor.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Processor.DIMENSION")
        }

        object Matcher {
            val COMPONENT = named("MaterialBreakerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialBreakerModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialBreakerModule.Name.Matcher.STYLE")
            val TEXT = named("MaterialBreakerModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Matcher.DIMENSION")
        }
    }

    internal operator fun invoke() = module {
        single<MaterialBreaker> { MaterialBreaker() }
        componentModule()
        contentModule()
        styleModule()
        textModule()
        colorModule()
        dimensionModule()
    }

    private fun Module.componentModule() {
        single<ComponentBreaker> { ComponentBreaker() }

        single<List<MatcherProtocol>>(Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearMatcher()
            )
        }

        single<List<Breaker>>(Name.Processor.COMPONENT) {
            emptyList()
        }
    }

    private fun Module.contentModule() {
        single<ContentBreaker> { ContentBreaker() }

        single<List<MatcherProtocol>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.CONTENT) {
            listOf(
                get<ComponentBreaker>()
            )
        }
    }

    private fun Module.styleModule() {
        single<StyleBreaker> { StyleBreaker() }

        single<List<MatcherProtocol>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.STYLE) {
            emptyList()
        }
    }

    private fun Module.textModule() {
        single<TextBreaker> { TextBreaker() }

        single<List<MatcherProtocol>>(Name.Matcher.TEXT) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.TEXT) {
            emptyList()
        }
    }

    private fun Module.colorModule() {
        single<ColorBreaker> { ColorBreaker() }

        single<List<MatcherProtocol>>(Name.Matcher.COLOR) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.COLOR) {
            emptyList()
        }
    }

    private fun Module.dimensionModule() {
        single<DimensionBreaker> { DimensionBreaker() }

        single<List<MatcherProtocol>>(Name.Matcher.DIMENSION) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.DIMENSION) {
            emptyList()
        }
    }
}


