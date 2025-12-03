package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ColorAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ContentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.DimensionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.OptionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StateAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StyleAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
object Material {

    fun ScopeDSL.invoke() {
        factory<List<AbstractAssembler>>(AssemblerModule.Name.ASSEMBLERS) {
            listOf(
                get<ComponentAssembler>(),
                get<ContentAssembler>(),
                get<TextAssembler>(),
                get<StateAssembler>(),
            )
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

    private fun ScopeDSL.componentModule() {
        scoped<ComponentAssembler> { ComponentAssembler(scope = this) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearItemsMatcher()
            )
        }

        scoped<List<AbstractAssembler>>(AssemblerModule.Name.Processor.COMPONENT) {
            listOf(
                get<ContentAssembler>(),
                get<StyleAssembler>(),
                get<OptionAssembler>(),
                get<StateAssembler>(),
            )
        }
    }

    private fun ScopeDSL.contentModule() {
        scoped<ContentAssembler> { ContentAssembler(scope = this) }

        scoped<List<AbstractAssembler>>(AssemblerModule.Name.Processor.CONTENT) {
            listOf(
                get<TextAssembler>(),
                get<ActionAssembler>(),
                get<ComponentAssembler>()
            )
        }
    }

    private fun ScopeDSL.styleModule() {
        scoped<StyleAssembler> { StyleAssembler(scope = this) }

        scoped<List<AbstractAssembler>>(AssemblerModule.Name.Processor.STYLE) {
            listOf(
                get<ColorAssembler>(),
                get<DimensionAssembler>()
            )
        }
    }

    private fun ScopeDSL.optionModule() {
        scoped<OptionAssembler> { OptionAssembler(scope = this) }

        scoped<List<AbstractAssembler>>(AssemblerModule.Name.Processor.OPTION) {
            emptyList()
        }
    }

    private fun ScopeDSL.stateModule() {
        scoped<StateAssembler> { StateAssembler(scope = this) }

        scoped<List<AbstractAssembler>>(AssemblerModule.Name.Processor.STATE) {
            listOf(
                get<TextAssembler>(),
            )
        }
    }

    private fun ScopeDSL.textModule() {
        scoped<TextAssembler> { TextAssembler(scope = this) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.TEXT) {
            emptyList()
        }
    }

    private fun ScopeDSL.colorModule() {
        scoped<ColorAssembler> { ColorAssembler(scope = this) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.COLOR) {
            emptyList()
        }
    }

    private fun ScopeDSL.dimensionModule() {
        scoped<DimensionAssembler> { DimensionAssembler(scope = this) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.DIMENSION) {
            emptyList()
        }
    }

    private fun ScopeDSL.actionModule() {
        scoped<ActionAssembler> { ActionAssembler(scope = this) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.ACTION) {
            emptyList()
        }
    }
}
