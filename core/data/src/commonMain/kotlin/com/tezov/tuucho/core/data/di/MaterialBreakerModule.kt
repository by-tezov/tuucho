package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser.breaker.AbstractBreaker
import com.tezov.tuucho.core.data.parser.breaker.ColorBreaker
import com.tezov.tuucho.core.data.parser.breaker.ComponentBreaker
import com.tezov.tuucho.core.data.parser.breaker.ContentBreaker
import com.tezov.tuucho.core.data.parser.breaker.DimensionBreaker
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.breaker.OptionBreaker
import com.tezov.tuucho.core.data.parser.breaker.StyleBreaker
import com.tezov.tuucho.core.data.parser.breaker.TextBreaker
import com.tezov.tuucho.core.data.parser.breaker._system.MatcherBreakerProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

// For now, breaker matcher and child processor are not used,
// only root and subs are broken into database
// TODO, if decided to not used it forever, code shrink should be done
object MaterialBreakerModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialBreakerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialBreakerModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialBreakerModule.Name.Processor.STYLE")
            val OPTION = named("MaterialBreakerModule.Name.Processor.OPTION")
            val TEXT = named("MaterialBreakerModule.Name.Processor.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Processor.DIMENSION")
        }

        object Matcher {
            val COMPONENT = named("MaterialBreakerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialBreakerModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialBreakerModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialBreakerModule.Name.Matcher.OPTION")
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
        optionModule()
        textModule()
        colorModule()
        dimensionModule()
    }

    private fun Module.componentModule() {
        single<ComponentBreaker> { ComponentBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.COMPONENT) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.COMPONENT) {
            emptyList()
        }
    }

    private fun Module.contentModule() {
        single<ContentBreaker> { ContentBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.CONTENT) {
            emptyList()
        }
    }

    private fun Module.styleModule() {
        single<StyleBreaker> { StyleBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.STYLE) {
            emptyList()
        }
    }

    private fun Module.optionModule() {
        single<OptionBreaker> { OptionBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.OPTION) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.OPTION) {
            emptyList()
        }
    }

    private fun Module.textModule() {
        single<TextBreaker> { TextBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.TEXT) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.TEXT) {
            emptyList()
        }
    }

    private fun Module.colorModule() {
        single<ColorBreaker> { ColorBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.COLOR) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.COLOR) {
            emptyList()
        }
    }

    private fun Module.dimensionModule() {
        single<DimensionBreaker> { DimensionBreaker() }

        single<List<MatcherBreakerProtocol>>(Name.Matcher.DIMENSION) {
            emptyList()
        }

        single<List<AbstractBreaker>>(Name.Processor.DIMENSION) {
            emptyList()
        }
    }

}


