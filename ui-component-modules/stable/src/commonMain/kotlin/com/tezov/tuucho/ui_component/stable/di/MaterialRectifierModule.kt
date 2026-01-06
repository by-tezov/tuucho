package com.tezov.tuucho.ui_component.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionAssociation
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
import com.tezov.tuucho.ui_component.stable.data.parser.rectifier.material.spacer.StyleSpacerDimensionRectifierMatcher
import org.koin.core.module.dsl.factoryOf

internal object MaterialRectifierModule {
    fun invoke() = scope(ModuleGroupData.Rectifier.ScopeContext.Material) {
        factoryOf(::StyleSpacerDimensionRectifierMatcher) associate DimensionAssociation.Matcher::class
    }
}
