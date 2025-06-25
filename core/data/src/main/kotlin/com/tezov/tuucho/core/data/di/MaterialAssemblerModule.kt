package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser.assembler.Assembler
import com.tezov.tuucho.core.data.parser.assembler.ColorAssembler
import com.tezov.tuucho.core.data.parser.assembler.ComponentAssembler
import com.tezov.tuucho.core.data.parser.assembler.ContentAssembler
import com.tezov.tuucho.core.data.parser.assembler.DimensionAssembler
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.assembler.StyleAssembler
import com.tezov.tuucho.core.data.parser.assembler.TextAssembler
import com.tezov.tuucho.core.data.parser.assembler._element.button.ContentButtonTextMatcher
import com.tezov.tuucho.core.data.parser.assembler._element.label.ContentLabelTextMatcher
import com.tezov.tuucho.core.data.parser.assembler._element.layout.linear.ContentLayoutLinearItemsMatcher
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialAssemblerModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialAssemblerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialAssemblerModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialAssemblerModule.Name.Processor.STYLE")
            val TEXT = named("MaterialAssemblerModule.Name.Processor.TEXT")
            val COLOR = named("MaterialAssemblerModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialAssemblerModule.Name.Processor.DIMENSION")
        }

        object Matcher {
            val COMPONENT = named("MaterialAssemblerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialAssemblerModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialAssemblerModule.Name.Matcher.STYLE")
            val TEXT = named("MaterialAssemblerModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialAssemblerModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialAssemblerModule.Name.Matcher.DIMENSION")
        }
    }

    internal operator fun invoke() = module {
        single<MaterialAssembler> {
            MaterialAssembler(
                database = get(),
                jsonConverter = get()
            )
        }
        componentModule()
        contentModule()
        styleModule()
        textModule()
        colorModule()
        dimensionModule()
    }

    private fun Module.componentModule() {
        single<ComponentAssembler> { ComponentAssembler() }

        single<List<Matcher>>(Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearItemsMatcher()
            )
        }

        single<List<Assembler>>(Name.Processor.COMPONENT) {
            listOf(
                get<ContentAssembler>(),
                get<StyleAssembler>(),
            )
        }
    }

    private fun Module.contentModule() {
        single<ContentAssembler> { ContentAssembler() }

        single<List<Matcher>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<Assembler>>(Name.Processor.CONTENT) {
            listOf(
                get<ComponentAssembler>(),
                get<TextAssembler>()
            )
        }
    }

    private fun Module.styleModule() {
        single<StyleAssembler> { StyleAssembler() }

        single<List<Matcher>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<Assembler>>(Name.Processor.STYLE) {
            emptyList()
        }
    }

    private fun Module.textModule() {
        single<TextAssembler> { TextAssembler() }

        single<List<Matcher>>(Name.Matcher.TEXT) {
            listOf(
                ContentLabelTextMatcher(),
                ContentButtonTextMatcher(),
            )
        }

        single<List<Assembler>>(Name.Processor.TEXT) {
            emptyList()
        }
    }

    private fun Module.colorModule() {
        single<ColorAssembler> { ColorAssembler() }

        single<List<Matcher>>(Name.Matcher.COLOR) {
            emptyList()
        }

        single<List<Assembler>>(Name.Processor.COLOR) {
            emptyList()
        }
    }

    private fun Module.dimensionModule() {
        single<DimensionAssembler> { DimensionAssembler() }

        single<List<Matcher>>(Name.Matcher.DIMENSION) {
            emptyList()
        }

        single<List<Assembler>>(Name.Processor.DIMENSION) {
            emptyList()
        }
    }


}


