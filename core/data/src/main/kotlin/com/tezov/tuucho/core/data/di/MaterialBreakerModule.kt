package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser._system.Breaker
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser.breaker.ColorBreaker
import com.tezov.tuucho.core.data.parser.breaker.ComponentBreaker
import com.tezov.tuucho.core.data.parser.breaker.ContentBreaker
import com.tezov.tuucho.core.data.parser.breaker.DimensionBreaker
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.breaker.OptionBreaker
import com.tezov.tuucho.core.data.parser.breaker.StyleBreaker
import com.tezov.tuucho.core.data.parser.breaker.TextBreaker
import com.tezov.tuucho.core.data.parser.breaker.layout.linear.LayoutLinearContentBreaker
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialBreakerModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialBreakerModule.Name.Processor.COMPONENT")
            val STYLE = named("MaterialBreakerModule.Name.Processor.STYLE")
            val OPTION = named("MaterialBreakerModule.Name.Processor.OPTION")
            val CONTENT = named("MaterialBreakerModule.Name.Processor.CONTENT")
            val TEXT = named("MaterialBreakerModule.Name.Processor.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Processor.DIMENSION")
        }

        object Matcher {
            val COMPONENT = named("MaterialBreakerModule.Name.Matcher.COMPONENT")
            val STYLE = named("MaterialBreakerModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialBreakerModule.Name.Matcher.OPTION")
            val CONTENT = named("MaterialBreakerModule.Name.Matcher.CONTENT")
            val TEXT = named("MaterialBreakerModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialBreakerModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialBreakerModule.Name.Matcher.DIMENSION")
        }
    }

    internal operator fun invoke() = module {

        single<MaterialBreaker> {
            MaterialBreaker()
        }

        // Component
        single<ComponentBreaker> {
            ComponentBreaker
        }

        single<List<Matcher>>(Name.Matcher.COMPONENT) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.COMPONENT) {
            listOf(
                get<StyleBreaker>(),
                get<OptionBreaker>(),
                get<ContentBreaker>(),
            )
        }

        // Content
        single<ContentBreaker> {
            ContentBreaker
        }

        single<List<Matcher>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.CONTENT) {
            listOf(
                LayoutLinearContentBreaker
            )
        }

        // Style
        single<StyleBreaker> {
            StyleBreaker
        }

        single<List<Matcher>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.STYLE) {
            emptyList()
        }

        // Option
        single<OptionBreaker> {
            OptionBreaker
        }

        single<List<Matcher>>(Name.Matcher.OPTION) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.OPTION) {
            emptyList()
        }

        // Text
        single<TextBreaker> {
            TextBreaker
        }

        single<List<Matcher>>(Name.Matcher.TEXT) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.TEXT) {
            emptyList()
        }

        // Dimension
        single<DimensionBreaker> {
            DimensionBreaker
        }

        single<List<Matcher>>(Name.Matcher.DIMENSION) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.DIMENSION) {
            emptyList()
        }

        // Color
        single<ColorBreaker> {
            ColorBreaker
        }

        single<List<Matcher>>(Name.Matcher.COLOR) {
            emptyList()
        }

        single<List<Breaker>>(Name.Processor.COLOR) {
            emptyList()
        }

    }
}


