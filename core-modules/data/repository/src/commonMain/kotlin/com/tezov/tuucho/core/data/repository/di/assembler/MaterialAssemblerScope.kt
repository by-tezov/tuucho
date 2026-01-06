package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ColorAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ContentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.DimensionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.OptionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StateAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StyleAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._element.layout.linear.ContentLayoutLinearItemsAssemblerMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

object MaterialAssemblerScope {
    fun invoke() = scope(ScopeContext.Material) {
        factory<Scope> { this }
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
        factoryOf(::ComponentAssembler)

        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearItemsAssemblerMatcher()
            )
        }

        // Used for contextual
        factory<List<AbstractAssembler>>(AssemblerModule.Name.Processor.COMPONENT) {
            listOf(
                get<ContentAssembler>(),
                get<StyleAssembler>(),
                get<OptionAssembler>(),
                get<StateAssembler>(),
            )
        }
    }

    private fun ScopeDSL.contentModule() {
        factoryOf(::ContentAssembler)

        factory<List<AbstractAssembler>>(AssemblerModule.Name.Processor.CONTENT) {
            listOf(
                get<TextAssembler>(),
                get<ActionAssembler>(),
                get<ComponentAssembler>()
            )
        }
    }

    private fun ScopeDSL.styleModule() {
        factoryOf(::StyleAssembler)

        factory<List<AbstractAssembler>>(AssemblerModule.Name.Processor.STYLE) {
            listOf(
                get<ColorAssembler>(),
                get<DimensionAssembler>()
            )
        }
    }

    private fun ScopeDSL.optionModule() {
        factoryOf(::OptionAssembler)

        factory<List<AbstractAssembler>>(AssemblerModule.Name.Processor.OPTION) {
            emptyList()
        }
    }

    private fun ScopeDSL.stateModule() {
        factoryOf(::StateAssembler)

        factory<List<AbstractAssembler>>(AssemblerModule.Name.Processor.STATE) {
            listOf(
                get<TextAssembler>(),
            )
        }
    }

    private fun ScopeDSL.textModule() {
        factoryOf(::TextAssembler)

        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.TEXT) {
            emptyList()
        }
    }

    private fun ScopeDSL.colorModule() {
        factoryOf(::ColorAssembler)

        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.COLOR) {
            emptyList()
        }
    }

    private fun ScopeDSL.dimensionModule() {
        factoryOf(::DimensionAssembler)

        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.DIMENSION) {
            emptyList()
        }
    }

    private fun ScopeDSL.actionModule() {
        factoryOf(::ActionAssembler)

        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.ACTION) {
            emptyList()
        }
    }
}
