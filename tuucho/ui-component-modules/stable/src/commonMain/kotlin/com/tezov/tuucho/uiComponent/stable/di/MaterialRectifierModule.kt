package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.form.FormValidatorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.scope
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.button.content.action.ActionButtonRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.button.content.label.ContentButtonLabelRectifier
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.form.field.content.ContentFormFieldTextErrorRectifier
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.form.field.content.ContentFormFieldTextRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.form.field.option.OptionFormFieldValidatorRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.form.field.state.StateFormFieldTextRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.label.content.ContentLabelTextRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.label.style.StyleLabelColorRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.label.style.StyleLabelDimensionRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.layout.linear.ContentLayoutLinearItemsRectifier
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.layout.linear.StyleLayoutLinearColorRectifierMatcher
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.spacer.StyleSpacerDimensionRectifierMatcher
import org.koin.dsl.ScopeDSL

internal object MaterialRectifierModule {
    fun invoke() = scope(ModuleContextData.Rectifier.ScopeContext.Material) {
        contentAssociation()
        textAssociation()
        colorAssociation()
        dimensionAssociation()
        actionAssociation()
        formValidatorModule()
    }

    private fun ScopeDSL.contentAssociation() {
        associate<ContentRectifier.Association.Processor> {
            factoryOf(::ContentLayoutLinearItemsRectifier)
            factoryOf(::ContentButtonLabelRectifier)
            factoryOf(::ContentFormFieldTextErrorRectifier)
        }
    }

    private fun ScopeDSL.textAssociation() {
        associate<TextRectifier.Association.Matcher> {
            factoryOf(::ContentLabelTextRectifierMatcher)
            factoryOf(::ContentFormFieldTextRectifierMatcher)
            factoryOf(::StateFormFieldTextRectifierMatcher)
        }
    }

    private fun ScopeDSL.colorAssociation() {
        associate<ColorRectifier.Association.Matcher> {
            factoryOf(::StyleLabelColorRectifierMatcher)
            factoryOf(::StyleLayoutLinearColorRectifierMatcher)
        }
    }

    private fun ScopeDSL.dimensionAssociation() {
        associate<DimensionRectifier.Association.Matcher> {
            factoryOf(::StyleSpacerDimensionRectifierMatcher)
            factoryOf(::StyleLabelDimensionRectifierMatcher)
        }
    }

    private fun ScopeDSL.actionAssociation() {
        associate<ActionRectifier.Association.Matcher> {
            factoryOf(::ActionButtonRectifierMatcher)
        }
    }

    private fun ScopeDSL.formValidatorModule() {
        associate<FormValidatorRectifier.Association.Matcher> {
            factoryOf(::OptionFormFieldValidatorRectifierMatcher)
        }
    }
}
