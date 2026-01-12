package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ColorAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ContentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.DimensionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.OptionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StateAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StyleAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
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
        stateAssociation()
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

        associate<MaterialAssembler.Association.Processor> {
            declaration<ComponentAssembler>()
            declaration<ContentAssembler>()
            declaration<TextAssembler>()
            declaration<StateAssembler>()
        }
    }

    private fun ScopeDSL.componentAssociation() {
        // Used for contextual
        associate<ComponentAssembler.Association.Processor> {
            declaration<ContentAssembler>()
            declaration<StyleAssembler>()
            declaration<OptionAssembler>()
            declaration<StateAssembler>()
        }
    }

    private fun ScopeDSL.contentAssociation() {
        associate<ContentAssembler.Association.Processor> {
            declaration<TextAssembler>()
            declaration<ActionAssembler>()
            declaration<ComponentAssembler>()
        }
    }

    private fun ScopeDSL.styleAssociation() {
        associate<StyleAssembler.Association.Processor> {
            declaration<ColorAssembler>()
            declaration<DimensionAssembler>()
        }
    }

    private fun ScopeDSL.stateAssociation() {
        declaration<TextAssembler>() associate StateAssembler.Association.Processor::class
    }
}
