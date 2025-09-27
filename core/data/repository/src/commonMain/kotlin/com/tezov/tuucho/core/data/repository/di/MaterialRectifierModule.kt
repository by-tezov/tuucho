package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.rectifier.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.ComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.OptionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.StateRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.StyleRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.button.content.action.ActionButtonMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.button.content.label.ContentButtonLabelMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.button.content.label.ContentButtonLabelRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.FormValidatorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.StateFormTextMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.field.content.ContentFormFieldTextErrorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.field.content.ContentFormFieldTextErrorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.field.content.ContentFormFieldTextMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.field.option.OptionFormFieldValidatorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.label.content.ContentLabelTextMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.label.style.StyleLabelColorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.label.style.StyleLabelDimensionMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.layout.linear.StyleLayoutLinearColorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.spacer.StyleSpacerDimensionMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.RectifierIdGenerator
import com.tezov.tuucho.core.data.repository.parser.rectifier.colors.ColorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.content.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.content.ContentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.dimensions.DimensionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.setting.component.SettingComponentNavigationRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.setting.component.SettingComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.texts.TextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.texts.TextsRectifier
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal object MaterialRectifierModule {

    object Name {

        object Processor {
            val COMPONENT = named("MaterialRectifierModule.Name.Processor.COMPONENT")
            val SETTING = named("MaterialRectifierModule.Name.Processor.SETTING")
            val CONTENT = named("MaterialRectifierModule.Name.Processor.CONTENT")
            val STYLE = named("MaterialRectifierModule.Name.Processor.STYLE")
            val OPTION = named("MaterialRectifierModule.Name.Processor.OPTION")
            val STATE = named("MaterialRectifierModule.Name.Processor.STATE")
            val TEXTS = named("MaterialRectifierModule.Name.Processor.TEXTS")
            val TEXT = named("MaterialRectifierModule.Name.Processor.TEXT")
            val COLORS = named("MaterialRectifierModule.Name.Processor.COLORS")
            val COLOR = named("MaterialRectifierModule.Name.Processor.COLOR")
            val DIMENSIONS = named("MaterialRectifierModule.Name.Processor.DIMENSIONS")
            val DIMENSION = named("MaterialRectifierModule.Name.Processor.DIMENSION")
            val ACTION = named("MaterialRectifierModule.Name.Processor.ACTION")
            val FIELD_VALIDATOR = named("MaterialRectifierModule.Name.Processor.FIELD_VALIDATOR")
        }

        object Matcher {
            val ID = named("MaterialRectifierModule.Name.Matcher.ID")
            val COMPONENT = named("MaterialRectifierModule.Name.Matcher.COMPONENT")
            val SETTING = named("MaterialRectifierModule.Name.Matcher.SETTING")
            val CONTENT = named("MaterialRectifierModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialRectifierModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialRectifierModule.Name.Matcher.OPTION")
            val STATE = named("MaterialRectifierModule.Name.Matcher.STATE")
            val TEXT = named("MaterialRectifierModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialRectifierModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialRectifierModule.Name.Matcher.DIMENSION")
            val ACTION = named("MaterialRectifierModule.Name.Matcher.ACTION")
            val FIELD_VALIDATOR = named("MaterialRectifierModule.Name.Matcher.FIELD_VALIDATOR")
        }
    }

    fun invoke() = module {
        single<MaterialRectifier> { MaterialRectifier() }

        idModule()
        componentModule()
        settingModule()
        contentModule()
        styleModule()
        optionModule()
        stateModule()
        textModule()
        colorModule()
        dimensionModule()
        actionModule()
        fieldValidatorModule()
    }

    private fun Module.idModule() {
        single<IdRectifier> {
            IdRectifier(
                idGenerator = RectifierIdGenerator()
            )
        }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.ID) {
            listOf(IdMatcher())
        }
    }

    private fun Module.componentModule() {
        single<ComponentRectifier> { ComponentRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.COMPONENT) {
            listOf(
                ContentLayoutLinearItemsMatcher(),
                ContentButtonLabelMatcher(),
            )
        }

        single<List<AbstractRectifier>>(Name.Processor.COMPONENT) {
            listOf(
                get<IdRectifier>(),
                get<SettingComponentRectifier>(),
                get<ContentRectifier>(),
                get<StyleRectifier>(),
                get<OptionRectifier>(),
            )
        }
    }

    private fun Module.settingModule() {
        single<SettingComponentRectifier> { SettingComponentRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.SETTING) {
            emptyList()
        }

        single<List<AbstractRectifier>>(Name.Processor.SETTING) {
            listOf(
                get<IdRectifier>(),
                SettingComponentNavigationRectifier(),
            )
        }
    }

    private fun Module.contentModule() {
        single<ContentRectifier> { ContentRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<AbstractRectifier>>(Name.Processor.CONTENT) {
            listOf(
                get<IdRectifier>(),
                ContentLayoutLinearItemsRectifier(),
                ContentButtonLabelRectifier(),
                ContentFormFieldTextErrorRectifier(),
                get<ActionRectifier>(),
                get<TextRectifier>(),
                get<ComponentRectifier>(),
            )
        }
    }

    private fun Module.styleModule() {
        single<StyleRectifier> { StyleRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<AbstractRectifier>>(Name.Processor.STYLE) {
            listOf(
                get<IdRectifier>(),
                get<ColorRectifier>(),
                get<DimensionRectifier>()
            )
        }
    }

    private fun Module.optionModule() {
        single<OptionRectifier> { OptionRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.OPTION) {
            emptyList()
        }

        single<List<AbstractRectifier>>(Name.Processor.OPTION) {
            listOf(
                get<IdRectifier>(),
                get<FormValidatorRectifier>(),
            )
        }
    }

    private fun Module.stateModule() {
        single<StateRectifier> { StateRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.STATE) {
            emptyList()
        }

        single<List<AbstractRectifier>>(Name.Processor.STATE) {
            listOf(
                get<IdRectifier>()
            )
        }
    }

    private fun Module.textModule() {
        single<TextsRectifier> { TextsRectifier() }

        single<List<AbstractRectifier>>(Name.Processor.TEXTS) {
            listOf(get<TextRectifier>())
        }

        single<TextRectifier> { TextRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.TEXT) {
            listOf(
                ContentLabelTextMatcher(),
                ContentFormFieldTextMatcher(),
                ContentFormFieldTextErrorMatcher(),
                StateFormTextMatcher(),
            )
        }

        single<List<AbstractRectifier>>(Name.Processor.TEXT) {
            listOf(get<IdRectifier>())
        }
    }

    private fun Module.colorModule() {
        single<ColorsRectifier> { ColorsRectifier() }

        single<List<AbstractRectifier>>(Name.Processor.COLORS) {
            listOf(get<ColorRectifier>())
        }

        single<ColorRectifier> { ColorRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.COLOR) {
            listOf(
                StyleLabelColorMatcher(),
                StyleLayoutLinearColorMatcher(),
            )
        }

        single<List<AbstractRectifier>>(Name.Processor.COLOR) {
            listOf(get<IdRectifier>())
        }
    }

    private fun Module.dimensionModule() {
        single<DimensionsRectifier> { DimensionsRectifier() }

        single<List<AbstractRectifier>>(Name.Processor.DIMENSIONS) {
            listOf(get<DimensionRectifier>())
        }

        single<DimensionRectifier> { DimensionRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.DIMENSION) {
            listOf(
                StyleLabelDimensionMatcher(),
                StyleSpacerDimensionMatcher(),
            )
        }

        single<List<AbstractRectifier>>(Name.Processor.DIMENSION) {
            listOf(get<IdRectifier>())
        }
    }

    private fun Module.actionModule() {
        single<ActionRectifier> { ActionRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.ACTION) {
            listOf(
                ActionButtonMatcher()
            )
        }

        single<List<AbstractRectifier>>(Name.Processor.ACTION) {
            emptyList()
        }
    }

    private fun Module.fieldValidatorModule() {
        single<FormValidatorRectifier> { FormValidatorRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.FIELD_VALIDATOR) {
            listOf(
                OptionFormFieldValidatorMatcher()
            )
        }

        single<List<AbstractRectifier>>(Name.Processor.FIELD_VALIDATOR) {
            emptyList()
        }
    }


}


