package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.button.content.action.ActionButtonMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.button.content.label.ContentButtonLabelRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.form.FormValidatorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.form.field.content.ContentFormFieldTextErrorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.form.field.content.ContentFormFieldTextMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.form.field.option.OptionFormFieldValidatorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.form.field.state.StateFormFieldTextMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.label.content.ContentLabelTextMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.label.style.StyleLabelColorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.label.style.StyleLabelDimensionMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.layout.linear.ContentLayoutLinearItemsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.layout.linear.StyleLayoutLinearColorMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.spacer.StyleSpacerDimensionMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierIdGenerator
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.option.OptionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.option.OptionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component.SettingComponentNavigationRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component.SettingComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.state.StateRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.state.StatesRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.style.StyleRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.style.StylesRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextsRectifier
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
internal object Material {
    object Name {
        object Processor {
            val SETTING get() = named("RectifierModule.Material.Name.Processor.SETTING")
        }
    }

    fun ScopeDSL.invoke() {
        factory<List<AbstractRectifier>>(RectifierModule.Name.RECTIFIERS) {
            listOf(
                get<ComponentsRectifier>(),
                get<ContentsRectifier>(),
                get<StylesRectifier>(),
                get<OptionsRectifier>(),
                get<StatesRectifier>(),
                get<TextsRectifier>(),
                get<ColorsRectifier>(),
                get<DimensionsRectifier>(),
                get<ActionsRectifier>(),
            )
        }
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

    private fun ScopeDSL.idModule() {
        factory<IdRectifier> {
            IdRectifier(
                scope = this,
                idGenerator = RectifierIdGenerator(
                    idGenerator = get()
                )
            )
        }

        factory<List<MatcherRectifierProtocol>>(RectifierModule.Name.Matcher.ID) {
            listOf(IdMatcher())
        }
    }

    private fun ScopeDSL.settingModule() {
        factory<SettingComponentRectifier> {
            SettingComponentRectifier(scope = this)
        }

        factory<List<AbstractRectifier>>(Name.Processor.SETTING) {
            listOf(
                get<IdRectifier>(),
                SettingComponentNavigationRectifier(scope = this),
            )
        }
    }

    private fun ScopeDSL.componentModule() {
        factory<ComponentsRectifier> {
            ComponentsRectifier(scope = this)
        }

        factory<ComponentRectifier> {
            ComponentRectifier(scope = this)
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.COMPONENT) {
            listOf(
                get<IdRectifier>(),
                get<SettingComponentRectifier>(),
                get<ContentRectifier>(),
                get<StyleRectifier>(),
                get<OptionRectifier>(),
                get<StateRectifier>(),
            )
        }
    }

    private fun ScopeDSL.contentModule() {
        factory<ContentsRectifier> {
            ContentsRectifier(scope = this)
        }

        factory<ContentRectifier> {
            ContentRectifier(scope = this)
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.CONTENT) {
            listOf(
                get<IdRectifier>(),
                get<ActionRectifier>(),
                get<TextRectifier>(),
                ContentLayoutLinearItemsRectifier(
                    scope = this,
                ),
                ContentButtonLabelRectifier(
                    scope = this,
                ),
                ContentFormFieldTextErrorRectifier(
                    scope = this,
                ),
            )
        }
    }

    private fun ScopeDSL.styleModule() {
        factory<StylesRectifier> {
            StylesRectifier(scope = this)
        }

        factory<StyleRectifier> {
            StyleRectifier(scope = this)
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.STYLE) {
            listOf(
                get<IdRectifier>(),
                get<ColorRectifier>(),
                get<DimensionRectifier>()
            )
        }
    }

    private fun ScopeDSL.optionModule() {
        factory<OptionsRectifier> {
            OptionsRectifier(scope = this)
        }

        factory<OptionRectifier> {
            OptionRectifier(scope = this)
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.OPTION) {
            listOf(
                get<IdRectifier>(),
                get<FormValidatorRectifier>(),
            )
        }
    }

    private fun ScopeDSL.stateModule() {
        factory<StatesRectifier> {
            StatesRectifier(scope = this)
        }

        factory<StateRectifier> {
            StateRectifier(scope = this)
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.STATE) {
            listOf(
                get<IdRectifier>(),
                get<TextRectifier>()
            )
        }
    }

    private fun ScopeDSL.textModule() {
        factory<TextsRectifier> {
            TextsRectifier(scope = this)
        }

        factory<TextRectifier> {
            TextRectifier(scope = this)
        }

        factory<List<MatcherRectifierProtocol>>(RectifierModule.Name.Matcher.TEXT) {
            listOf(
                ContentLabelTextMatcher(),
                ContentFormFieldTextMatcher(),
                StateFormFieldTextMatcher(),
            )
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.TEXT) {
            listOf(get<IdRectifier>())
        }
    }

    private fun ScopeDSL.colorModule() {
        factory<ColorsRectifier> {
            ColorsRectifier(scope = this)
        }

        factory<ColorRectifier> {
            ColorRectifier(scope = this)
        }

        factory<List<MatcherRectifierProtocol>>(RectifierModule.Name.Matcher.COLOR) {
            listOf(
                StyleLabelColorMatcher(),
                StyleLayoutLinearColorMatcher(),
            )
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.COLOR) {
            listOf(get<IdRectifier>())
        }
    }

    private fun ScopeDSL.dimensionModule() {
        factory<DimensionsRectifier> {
            DimensionsRectifier(scope = this)
        }

        factory<DimensionRectifier> {
            DimensionRectifier(scope = this)
        }

        factory<List<MatcherRectifierProtocol>>(RectifierModule.Name.Matcher.DIMENSION) {
            listOf(
                StyleLabelDimensionMatcher(),
                StyleSpacerDimensionMatcher(),
            )
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.DIMENSION) {
            listOf(get<IdRectifier>())
        }
    }

    private fun ScopeDSL.actionModule() {
        factory<ActionsRectifier> {
            ActionsRectifier(scope = this)
        }

        factory<ActionRectifier> {
            ActionRectifier(scope = this)
        }

        factory<List<MatcherRectifierProtocol>>(RectifierModule.Name.Matcher.ACTION) {
            listOf(
                ActionButtonMatcher()
            )
        }

        factory<List<AbstractRectifier>>(RectifierModule.Name.Processor.ACTION) {
            listOf(get<IdRectifier>())
        }
    }

    private fun ScopeDSL.fieldValidatorModule() {
        factory<FormValidatorRectifier> {
            FormValidatorRectifier(scope = this)
        }

        factory<List<MatcherRectifierProtocol>>(RectifierModule.Name.Matcher.FIELD_VALIDATOR) {
            listOf(
                OptionFormFieldValidatorMatcher()
            )
        }
    }
}
