package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.scope
import com.tezov.tuucho.sample.uiExtension.data.rectifier.material.label.content.ActionCustomLabelRectifierMatcher
import com.tezov.tuucho.sample.uiExtension.data.rectifier.material.label.content.ContentCustomLabelTextRectifierMatcher
import com.tezov.tuucho.sample.uiExtension.data.rectifier.material.label.style.StyleCustomLabelColorRectifierMatcher
import com.tezov.tuucho.sample.uiExtension.data.rectifier.material.label.style.StyleCustomLabelDimensionRectifierMatcher
import org.koin.dsl.ScopeDSL

internal object MaterialRectifierModule {
    fun invoke() = scope(ModuleContextData.Rectifier.ScopeContext.Material) {
        textAssociation()
        colorAssociation()
        dimensionAssociation()
        actionAssociation()
    }

    private fun ScopeDSL.textAssociation() {
        associate<TextRectifier.Association.Matcher> {
            factoryOf(::ContentCustomLabelTextRectifierMatcher)
        }
    }

    private fun ScopeDSL.colorAssociation() {
        associate<ColorRectifier.Association.Matcher> {
            factoryOf(::StyleCustomLabelColorRectifierMatcher)
        }
    }

    private fun ScopeDSL.dimensionAssociation() {
        associate<DimensionRectifier.Association.Matcher> {
            factoryOf(::StyleCustomLabelDimensionRectifierMatcher)
        }
    }

    private fun ScopeDSL.actionAssociation() {
        associate<ActionRectifier.Association.Matcher> {
            factoryOf(::ActionCustomLabelRectifierMatcher)
        }
    }
}
