package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ColorAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssociation
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ContentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ContentAssociation
import com.tezov.tuucho.core.data.repository.parser.assembler.material.DimensionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssociation
import com.tezov.tuucho.core.data.repository.parser.assembler.material.OptionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StateAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StateAssociation
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StyleAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StyleAssociation
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._element.layout.linear.ContentLayoutLinearItemsAssemblerMatcher
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

object MaterialAssemblerScope {
    fun invoke() = scope(ScopeContext.Material) {
        factory<Scope> { this }
        assemblers()
        componentAssociation()
        contentAssociation()
        styleAssociation()
        optionAssociation()
        stateAssociation()
        textAssociation()
        colorAssociation()
        dimensionAssociation()
        actionAssociation()
    }

    private fun ScopeDSL.assemblers() {
        factoryOf(::ComponentAssembler)
        factoryOf(::ContentAssembler)
        factoryOf(::StyleAssembler)
        factoryOf(::OptionAssembler)
        factoryOf(::StateAssembler)
        factoryOf(::TextAssembler)
        factoryOf(::ColorAssembler)
        factoryOf(::DimensionAssembler)
        factoryOf(::ActionAssembler)

        associate<MaterialAssociation.Assembler> {
            declaration<ComponentAssembler>()
            declaration<ContentAssembler>()
            declaration<TextAssembler>()
            declaration<StateAssembler>()
        }
    }

    private fun ScopeDSL.componentAssociation() {
        // matchers
        factoryOf(::ContentLayoutLinearItemsAssemblerMatcher) associate ComponentAssociation.Matcher::class

        // Used for contextual
        associate<ComponentAssociation.Assembler> {
            declaration<ContentAssembler>()
            declaration<StyleAssembler>()
            declaration<OptionAssembler>()
            declaration<StateAssembler>()

        }
    }

    private fun ScopeDSL.contentAssociation() {
        // assemblers
        associate<ContentAssociation.Assembler> {
            declaration<TextAssembler>()
            declaration<ActionAssembler>()
            declaration<ComponentAssembler>()
        }
    }

    private fun ScopeDSL.styleAssociation() {
        // assemblers
        associate<StyleAssociation.Assembler> {
            declaration<ColorAssembler>()
            declaration<DimensionAssembler>()
        }
    }

    private fun ScopeDSL.optionAssociation() {

    }

    private fun ScopeDSL.stateAssociation() {
        // assemblers
        declaration<TextAssembler>() associate StateAssociation.Assembler::class
    }

    private fun ScopeDSL.textAssociation() {

    }

    private fun ScopeDSL.colorAssociation() {

    }

    private fun ScopeDSL.dimensionAssociation() {

    }

    private fun ScopeDSL.actionAssociation() {

    }
}
