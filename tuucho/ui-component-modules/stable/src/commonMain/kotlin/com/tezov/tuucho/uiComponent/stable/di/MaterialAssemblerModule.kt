package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.scope
import com.tezov.tuucho.uiComponent.stable.data.parser.assembler.material.layout.linear.ContentLayoutLinearItemsAssemblerMatcher
import org.koin.dsl.ScopeDSL

internal object MaterialAssemblerModule {
    fun invoke() = scope(ModuleContextData.Assembler.ScopeContext.Material) {
        componentAssociation()
    }

    private fun ScopeDSL.componentAssociation() {
        associate<ComponentAssembler.Association.Matcher> {
            factoryOf(::ContentLayoutLinearItemsAssemblerMatcher)
        }
    }
}
