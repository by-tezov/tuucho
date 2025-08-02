package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser._system.IdGenerator

import com.tezov.tuucho.core.data.parser.rectifier.ActionRectifier
import com.tezov.tuucho.core.data.parser.rectifier.ComponentRectifier
import com.tezov.tuucho.core.data.parser.rectifier.ContentRectifier
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.parser.rectifier.OptionRectifier
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.data.parser.rectifier.StateRectifier
import com.tezov.tuucho.core.data.parser.rectifier.StyleRectifier
import com.tezov.tuucho.core.data.parser.rectifier.ValidatorRectifier
import com.tezov.tuucho.core.data.parser.rectifier._element.button.content.action.ActionButtonMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.button.content.label.ContentButtonLabelMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.button.content.label.ContentButtonLabelRectifier
import com.tezov.tuucho.core.data.parser.rectifier._element.form.StateFormTextMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.form.field.content.ContentFormFieldTextErrorMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.form.field.content.ContentFormFieldTextErrorRectifier
import com.tezov.tuucho.core.data.parser.rectifier._element.form.field.content.ContentFormFieldTextMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.form.field.option.OptionFormFieldValidatorMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.label.content.ContentLabelTextMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.label.style.StyleLabelColorMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.label.style.StyleLabelDimensionMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsRectifier
import com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear.StyleLayoutLinearColorMatcher
import com.tezov.tuucho.core.data.parser.rectifier._element.spacer.StyleSpacerDimensionMatcher
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorRectifier
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.id.IdMatcher
import com.tezov.tuucho.core.data.parser.rectifier.id.IdRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsRectifier
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialRectifierModule {

    object Name {

        object Processor {
            val COMPONENT = named("MaterialRectifierModule.Name.Processor.COMPONENT")
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
            val VALIDATOR = named("MaterialRectifierModule.Name.Processor.VALIDATOR")
        }

        object Matcher {
            val ID = named("MaterialRectifierModule.Name.Matcher.ID")
            val COMPONENT = named("MaterialRectifierModule.Name.Matcher.COMPONENT")
            val CONTENT = named("MaterialRectifierModule.Name.Matcher.CONTENT")
            val STYLE = named("MaterialRectifierModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialRectifierModule.Name.Matcher.OPTION")
            val STATE = named("MaterialRectifierModule.Name.Matcher.STATE")
            val TEXT = named("MaterialRectifierModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialRectifierModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialRectifierModule.Name.Matcher.DIMENSION")
            val ACTION = named("MaterialRectifierModule.Name.Matcher.ACTION")
            val VALIDATOR = named("MaterialRectifierModule.Name.Matcher.VALIDATOR")
        }
    }

    internal operator fun invoke() = module {
        single<MaterialRectifier> { MaterialRectifier() }
        idModule()
        componentModule()
        contentModule()
        styleModule()
        optionModule()
        stateModule()
        textModule()
        colorModule()
        dimensionModule()
        actionModule()
        validatorModule()
    }

    private fun Module.idModule() {
        single<IdGenerator> { IdGenerator() }

        single<IdRectifier> {
            IdRectifier(
                get<IdGenerator>()
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

        single<List<Rectifier>>(Name.Processor.COMPONENT) {
            listOf(
                get<IdRectifier>(),
                get<ContentRectifier>(),
                get<StyleRectifier>(),
                get<OptionRectifier>(),
            )
        }
    }

    private fun Module.contentModule() {
        single<ContentRectifier> { ContentRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.CONTENT) {
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

        single<List<Rectifier>>(Name.Processor.STYLE) {
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

        single<List<Rectifier>>(Name.Processor.OPTION) {
            listOf(
                get<IdRectifier>(),
                get<ValidatorRectifier>(),
            )
        }
    }

    private fun Module.stateModule() {
        single<StateRectifier> { StateRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.STATE) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.STATE) {
            listOf(
                get<IdRectifier>()
            )
        }
    }

    private fun Module.textModule() {
        single<TextsRectifier> { TextsRectifier() }

        single<List<Rectifier>>(Name.Processor.TEXTS) {
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

        single<List<Rectifier>>(Name.Processor.TEXT) {
            listOf(get<IdRectifier>())
        }
    }

    private fun Module.colorModule() {
        single<ColorsRectifier> { ColorsRectifier() }

        single<List<Rectifier>>(Name.Processor.COLORS) {
            listOf(get<ColorRectifier>())
        }

        single<ColorRectifier> { ColorRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.COLOR) {
            listOf(
                StyleLabelColorMatcher(),
                StyleLayoutLinearColorMatcher(),
            )
        }

        single<List<Rectifier>>(Name.Processor.COLOR) {
            listOf(get<IdRectifier>())
        }
    }

    private fun Module.dimensionModule() {
        single<DimensionsRectifier> { DimensionsRectifier() }

        single<List<Rectifier>>(Name.Processor.DIMENSIONS) {
            listOf(get<DimensionRectifier>())
        }

        single<DimensionRectifier> { DimensionRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.DIMENSION) {
            listOf(
                StyleLabelDimensionMatcher(),
                StyleSpacerDimensionMatcher(),
            )
        }

        single<List<Rectifier>>(Name.Processor.DIMENSION) {
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

        single<List<Rectifier>>(Name.Processor.ACTION) {
            emptyList()
        }
    }

    private fun Module.validatorModule() {
        single<ValidatorRectifier> { ValidatorRectifier() }

        single<List<MatcherRectifierProtocol>>(Name.Matcher.VALIDATOR) {
            listOf(
                OptionFormFieldValidatorMatcher()
            )
        }

        single<List<Rectifier>>(Name.Processor.VALIDATOR) {
            emptyList()
        }
    }


}


