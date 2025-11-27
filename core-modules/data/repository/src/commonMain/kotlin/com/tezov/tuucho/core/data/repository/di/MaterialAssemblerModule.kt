package com.tezov.tuucho.core.data.repository.di

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
import com.tezov.tuucho.core.data.repository.parser.assembler._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.qualifier.named

@OptIn(TuuchoExperimentalAPI::class)
internal object MaterialAssemblerModule {
    object Name {
        val ASSEMBLERS = named("MaterialAssemblerModule.Name.ASSEMBLERS")

        object Processor {
            val COMPONENT = named("MaterialAssemblerModule.Name.Processor.COMPONENT")
            val CONTENT = named("MaterialAssemblerModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialAssemblerModule.Name.Processor.STYLE")
            val OPTION = named("MaterialAssemblerModule.Name.Processor.OPTION")
            val STATE = named("MaterialAssemblerModule.Name.Processor.STATE")
        }

        object Matcher {
            val COMPONENT = named("MaterialAssemblerModule.Name.Matcher.COMPONENT")
        }
    }

    fun invoke() = module(ModuleGroupData.Assembler) {
        factory<List<AbstractAssembler>>(Name.ASSEMBLERS) {
            listOf(
                get<ComponentAssembler>(),
                get<ContentAssembler>(),
                get<TextAssembler>(),
                get<StateAssembler>(),
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
        finalModule()
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

        single<List<AbstractAssembler>>(Name.Processor.STYLE) {
            listOf(
                get<ColorAssembler>(),
                get<DimensionAssembler>()
            )
        }
    }

    private fun Module.optionModule() {
        single<OptionAssembler> { OptionAssembler() }

        single<List<AbstractAssembler>>(Name.Processor.OPTION) {
            emptyList()
        }
    }

    private fun Module.stateModule() {
        single<StateAssembler> { StateAssembler() }

        single<List<AbstractAssembler>>(Name.Processor.STATE) {
            listOf(
                get<TextAssembler>(),
            )
        }
    }

    private fun Module.finalModule() {
        single<TextAssembler> { TextAssembler() }
        single<ColorAssembler> { ColorAssembler() }
        single<DimensionAssembler> { DimensionAssembler() }
        single<ActionAssembler> { ActionAssembler() }
    }
}
