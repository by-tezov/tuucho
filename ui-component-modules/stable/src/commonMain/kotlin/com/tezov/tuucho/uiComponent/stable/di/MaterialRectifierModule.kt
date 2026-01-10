package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.form.FieldValidatorAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextAssociation
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
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
    fun invoke() = scope(ModuleGroupData.Rectifier.ScopeContext.Material) {
        contentAssociation()
        textAssociation()
        colorAssociation()
        dimensionAssociation()
        actionAssociation()
        fieldValidatorModule()
    }

    private fun ScopeDSL.contentAssociation() {
        associate<ContentAssociation.Rectifier> {
            factoryOf(::ContentLayoutLinearItemsRectifier)
            factoryOf(::ContentButtonLabelRectifier)
            factoryOf(::ContentFormFieldTextErrorRectifier)
        }
    }

    private fun ScopeDSL.textAssociation() {
        associate<TextAssociation.Matcher> {
            factoryOf(::ContentLabelTextRectifierMatcher)
            factoryOf(::ContentFormFieldTextRectifierMatcher)
            factoryOf(::StateFormFieldTextRectifierMatcher)
        }
    }

    private fun ScopeDSL.colorAssociation() {
        associate<ColorAssociation.Matcher> {
            factoryOf(::StyleLabelColorRectifierMatcher)
            factoryOf(::StyleLayoutLinearColorRectifierMatcher)
        }
    }

    private fun ScopeDSL.dimensionAssociation() {
        associate<DimensionAssociation.Matcher> {
            factoryOf(::StyleSpacerDimensionRectifierMatcher)
            factoryOf(::StyleLabelDimensionRectifierMatcher)
        }
    }

    private fun ScopeDSL.actionAssociation() {
        associate<ActionAssociation.Matcher> {
            factoryOf(::ActionButtonRectifierMatcher)
        }
    }

    private fun ScopeDSL.fieldValidatorModule() {
        associate<FieldValidatorAssociation.Matcher> {
            factoryOf(::OptionFormFieldValidatorRectifierMatcher)
        }
    }
}
