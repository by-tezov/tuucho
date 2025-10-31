package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.breaker.ActionBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.ColorBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.ComponentBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.ContentBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.DimensionBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.OptionBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.StateBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.StyleBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker.TextBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker._system.AbstractBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker._system.MatcherBreakerProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named

internal object MaterialBreakerModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialBreakerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialBreakerModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialBreakerModule.Name.Processor.STYLE")
            val OPTION = named("MaterialBreakerModule.Name.Processor.OPTION")
            val STATE = named("MaterialBreakerModule.Name.Processor.STATE")
            val TEXT = named("MaterialBreakerModule.Name.Processor.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Processor.DIMENSION")
            val ACTION = named("MaterialBreakerModule.Name.Processor.ACTION")
        }

        object Matcher {
            val COMPONENT = named("MaterialBreakerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialBreakerModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialBreakerModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialBreakerModule.Name.Matcher.OPTION")
            val STATE = named("MaterialBreakerModule.Name.Matcher.STATE")
            val TEXT = named("MaterialBreakerModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Matcher.DIMENSION")
            val ACTION = named("MaterialBreakerModule.Name.Matcher.ACTION")
        }
    }

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.Breaker

        override fun Module.declaration() {
            single<MaterialBreaker> { MaterialBreaker() }
            componentModule()
            contentModule()
            styleModule()
            optionModule()
            stateModule()
            textModule()
            colorModule()
            dimensionModule()
            actionModule()
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

        private fun Module.stateModule() {
            single<StateBreaker> { StateBreaker() }

            single<List<MatcherBreakerProtocol>>(Name.Matcher.STATE) {
                emptyList()
            }

            single<List<AbstractBreaker>>(Name.Processor.STATE) {
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

        private fun Module.actionModule() {
            single<ActionBreaker> { ActionBreaker() }

            single<List<MatcherBreakerProtocol>>(Name.Matcher.ACTION) {
                emptyList()
            }

            single<List<AbstractBreaker>>(Name.Processor.ACTION) {
                emptyList()
            }
        }
    }
}


