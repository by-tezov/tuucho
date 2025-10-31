package com.tezov.tuucho.core.data.repository.di


import com.tezov.tuucho.core.data.repository.parser.assembler.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.ColorAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.ContentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.DimensionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.OptionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.StateAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.StyleAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named

internal object MaterialAssemblerModule {

    object Name {
        val ASSEMBLERS = named("MaterialAssemblerModule.Name.ASSEMBLERS")

        object Processor {
            val COMPONENT = named("MaterialAssemblerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialAssemblerModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialAssemblerModule.Name.Processor.STYLE")
            val OPTION = named("MaterialAssemblerModule.Name.Processor.OPTION")
            val STATE = named("MaterialAssemblerModule.Name.Processor.STATE")
            val TEXT = named("MaterialAssemblerModule.Name.Processor.TEXT")
            val COLOR = named("MaterialAssemblerModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialAssemblerModule.Name.Processor.DIMENSION")
            val ACTION = named("MaterialAssemblerModule.Name.Processor.ACTION")
        }

        object Matcher {
            val COMPONENT = named("MaterialAssemblerModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialAssemblerModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialAssemblerModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialAssemblerModule.Name.Matcher.OPTION")
            val STATE = named("MaterialAssemblerModule.Name.Matcher.STATE")
            val TEXT = named("MaterialAssemblerModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialAssemblerModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialAssemblerModule.Name.Matcher.DIMENSION")
            val ACTION = named("MaterialAssemblerModule.Name.Matcher.ACTION")
        }
    }

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.Assembler

        override fun Module.declaration() {
            factory<List<AbstractAssembler>>(Name.ASSEMBLERS) {
                listOf(
                    get<ComponentAssembler>(),
                    get<ContentAssembler>(),
                    get<StateAssembler>(),
                    get<TextAssembler>(),
                )
            }

            single<MaterialAssembler> {
                MaterialAssembler()
            }
            single<JsonObjectMerger> {
                JsonObjectMerger()
            }

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
            single<ComponentAssembler> { ComponentAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.COMPONENT) {
                listOf(
                    ContentLayoutLinearItemsMatcher()
                )
            }

            single<List<AbstractAssembler>>(Name.Processor.COMPONENT) {
                listOf(
                    get<ContentAssembler>(),
                    get<StyleAssembler>(),
                    get<OptionAssembler>(),
                    get<StateAssembler>(),
                )
            }
        }

        private fun Module.contentModule() {
            single<ContentAssembler> { ContentAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.CONTENT) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.CONTENT) {
                listOf(
                    get<TextAssembler>(),
                    get<ActionAssembler>(),
                    get<ComponentAssembler>()
                )
            }
        }

        private fun Module.styleModule() {
            single<StyleAssembler> { StyleAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.STYLE) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.STYLE) {
                listOf(
                    get<ColorAssembler>(),
                    get<DimensionAssembler>()
                )
            }
        }

        private fun Module.optionModule() {
            single<OptionAssembler> { OptionAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.OPTION) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.OPTION) {
                emptyList()
            }
        }

        private fun Module.stateModule() {
            single<StateAssembler> { StateAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.STATE) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.STATE) {
                emptyList()
            }
        }

        private fun Module.textModule() {
            single<TextAssembler> { TextAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.TEXT) {
                listOf()
            }

            single<List<AbstractAssembler>>(Name.Processor.TEXT) {
                emptyList()
            }
        }

        private fun Module.colorModule() {
            single<ColorAssembler> { ColorAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.COLOR) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.COLOR) {
                emptyList()
            }
        }

        private fun Module.dimensionModule() {
            single<DimensionAssembler> { DimensionAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.DIMENSION) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.DIMENSION) {
                emptyList()
            }
        }

        private fun Module.actionModule() {
            single<ActionAssembler> { ActionAssembler() }

            single<List<MatcherAssemblerProtocol>>(Name.Matcher.ACTION) {
                emptyList()
            }

            single<List<AbstractAssembler>>(Name.Processor.ACTION) {
                emptyList()
            }
        }
    }
}


