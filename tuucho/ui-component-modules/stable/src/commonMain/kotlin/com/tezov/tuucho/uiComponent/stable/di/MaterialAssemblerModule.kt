package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ImageAssembler
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.scope
import com.tezov.tuucho.uiComponent.stable.data.parser.assembler.material.image.ContentImageValuesAssemblerMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.assembler.material.layout.linear.ContentLayoutLinearItemsAssemblerMatcher
import org.koin.dsl.ScopeDSL

internal object MaterialAssemblerModule {
    fun invoke() = scope(ModuleContextData.Assembler.ScopeContext.Material) {
        componentAssociation()
        imageAssociation()
    }

    private fun ScopeDSL.componentAssociation() {
        associate<ComponentAssembler.Association.Matcher> {
            factoryOf(::ContentLayoutLinearItemsAssemblerMatcher)
        }
    }

    private fun ScopeDSL.imageAssociation() {
        associate<ImageAssembler.Association.Matcher> {
            factoryOf(::ContentImageValuesAssemblerMatcher)
        }
    }
}
