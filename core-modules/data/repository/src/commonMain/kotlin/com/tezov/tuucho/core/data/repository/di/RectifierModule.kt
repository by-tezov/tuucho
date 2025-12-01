package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
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
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
internal object RectifierModule {
    fun invoke() = module(ModuleGroupData.Rectifier) {
        // Material
        scope<MaterialRectifier> {
            Material.run { invoke() }
        }
        single<MaterialRectifier> {
            MaterialRectifier()
        } withOptions {
            onClose { it?.close() }
        }
        factory<Scope>(Material.Name.RECTIFIERS_SCOPE) {
            get<MaterialRectifier>().scope
        }

        // Response
        scope<ResponseRectifier> {
            Response.run { invoke() }
        }
        single<ResponseRectifier> {
            ResponseRectifier()
        } withOptions {
            onClose { it?.close() }
        }
        factory<Scope>(Response.Name.RECTIFIERS_SCOPE) {
            get<ResponseRectifier>().scope
        }
    }

    object Material {
        object Name {
            val RECTIFIERS = named("RectifierModule.Material.Name.RECTIFIERS")
            val RECTIFIERS_SCOPE = named("RectifierModule.Material.Name.RECTIFIERS_SCOPE")

            object Processor {
                val COMPONENT = named("MaterialRectifierModule.Name.Processor.COMPONENT")
                val SETTING = named("MaterialRectifierModule.Name.Processor.SETTING")
                val CONTENT = named("MaterialRectifierModule.Name.Processor.CONTENT")
                val STYLE = named("MaterialRectifierModule.Name.Processor.STYLE")
                val OPTION = named("MaterialRectifierModule.Name.Processor.OPTION")
                val STATE = named("MaterialRectifierModule.Name.Processor.STATE")
                val TEXT = named("MaterialRectifierModule.Name.Processor.TEXT")
                val COLOR = named("MaterialRectifierModule.Name.Processor.COLOR")
                val DIMENSION = named("MaterialRectifierModule.Name.Processor.DIMENSION")
                val ACTION = named("MaterialRectifierModule.Name.Processor.ACTION")
            }

            object Matcher {
                val ID = named("MaterialRectifierModule.Name.Matcher.ID")
                val TEXT = named("MaterialRectifierModule.Name.Matcher.TEXT")
                val COLOR = named("MaterialRectifierModule.Name.Matcher.COLOR")
                val DIMENSION = named("MaterialRectifierModule.Name.Matcher.DIMENSION")
                val ACTION = named("MaterialRectifierModule.Name.Matcher.ACTION")
                val FIELD_VALIDATOR = named("MaterialRectifierModule.Name.Matcher.FIELD_VALIDATOR")
            }
        }

        fun ScopeDSL.invoke() {
            factory<List<AbstractRectifier>>(Name.RECTIFIERS) {
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
            scoped<IdRectifier> {
                IdRectifier(
                    scope = get(Name.RECTIFIERS_SCOPE),
                    idGenerator = RectifierIdGenerator(
                        idGenerator = get()
                    )
                )
            }

            scoped<List<MatcherRectifierProtocol>>(Name.Matcher.ID) {
                listOf(IdMatcher())
            }
        }

        private fun ScopeDSL.settingModule() {
            scoped<SettingComponentRectifier> {
                SettingComponentRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<AbstractRectifier>>(Name.Processor.SETTING) {
                listOf(
                    get<IdRectifier>(),
                    SettingComponentNavigationRectifier(scope = get(Name.RECTIFIERS_SCOPE)),
                )
            }
        }

        private fun ScopeDSL.componentModule() {
            scoped<ComponentsRectifier> {
                ComponentsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<ComponentRectifier> {
                ComponentRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<AbstractRectifier>>(Name.Processor.COMPONENT) {
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
            scoped<ContentsRectifier> {
                ContentsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<ContentRectifier> {
                ContentRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<AbstractRectifier>>(Name.Processor.CONTENT) {
                listOf(
                    get<IdRectifier>(),
                    get<ActionRectifier>(),
                    get<TextRectifier>(),
                    ContentLayoutLinearItemsRectifier(
                        scope = get(Name.RECTIFIERS_SCOPE),
                    ),
                    ContentButtonLabelRectifier(
                        scope = get(Name.RECTIFIERS_SCOPE),
                    ),
                    ContentFormFieldTextErrorRectifier(
                        scope = get(Name.RECTIFIERS_SCOPE),
                    ),
                )
            }
        }

        private fun ScopeDSL.styleModule() {
            scoped<StylesRectifier> {
                StylesRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<StyleRectifier> {
                StyleRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<AbstractRectifier>>(Name.Processor.STYLE) {
                listOf(
                    get<IdRectifier>(),
                    get<ColorRectifier>(),
                    get<DimensionRectifier>()
                )
            }
        }

        private fun ScopeDSL.optionModule() {
            scoped<OptionsRectifier> {
                OptionsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<OptionRectifier> {
                OptionRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<AbstractRectifier>>(Name.Processor.OPTION) {
                listOf(
                    get<IdRectifier>(),
                    get<FormValidatorRectifier>(),
                )
            }
        }

        private fun ScopeDSL.stateModule() {
            scoped<StatesRectifier> {
                StatesRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<StateRectifier> {
                StateRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<AbstractRectifier>>(Name.Processor.STATE) {
                listOf(
                    get<IdRectifier>(),
                    get<TextRectifier>()
                )
            }
        }

        private fun ScopeDSL.textModule() {
            scoped<TextsRectifier> {
                TextsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<TextRectifier> {
                TextRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<MatcherRectifierProtocol>>(Name.Matcher.TEXT) {
                listOf(
                    ContentLabelTextMatcher(),
                    ContentFormFieldTextMatcher(),
                    StateFormFieldTextMatcher(),
                )
            }

            scoped<List<AbstractRectifier>>(Name.Processor.TEXT) {
                listOf(get<IdRectifier>())
            }
        }

        private fun ScopeDSL.colorModule() {
            scoped<ColorsRectifier> {
                ColorsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<ColorRectifier> {
                ColorRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<MatcherRectifierProtocol>>(Name.Matcher.COLOR) {
                listOf(
                    StyleLabelColorMatcher(),
                    StyleLayoutLinearColorMatcher(),
                )
            }

            scoped<List<AbstractRectifier>>(Name.Processor.COLOR) {
                listOf(get<IdRectifier>())
            }
        }

        private fun ScopeDSL.dimensionModule() {
            scoped<DimensionsRectifier> {
                DimensionsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<DimensionRectifier> {
                DimensionRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<MatcherRectifierProtocol>>(Name.Matcher.DIMENSION) {
                listOf(
                    StyleLabelDimensionMatcher(),
                    StyleSpacerDimensionMatcher(),
                )
            }

            scoped<List<AbstractRectifier>>(Name.Processor.DIMENSION) {
                listOf(get<IdRectifier>())
            }
        }

        private fun ScopeDSL.actionModule() {
            scoped<ActionsRectifier> {
                ActionsRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<ActionRectifier> {
                ActionRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<MatcherRectifierProtocol>>(Name.Matcher.ACTION) {
                listOf(
                    ActionButtonMatcher()
                )
            }

            scoped<List<AbstractRectifier>>(Name.Processor.ACTION) {
                listOf(get<IdRectifier>())
            }
        }

        private fun ScopeDSL.fieldValidatorModule() {
            scoped<FormValidatorRectifier> {
                FormValidatorRectifier(scope = get(Name.RECTIFIERS_SCOPE))
            }

            scoped<List<MatcherRectifierProtocol>>(Name.Matcher.FIELD_VALIDATOR) {
                listOf(
                    OptionFormFieldValidatorMatcher()
                )
            }
        }
    }

    object Response {
        object Name {
            val RECTIFIERS = named("RectifierModule.Response.Name.RECTIFIERS")
            val RECTIFIERS_SCOPE = named("RectifierModule.Response.Name.RECTIFIERS_SCOPE")
        }

        fun ScopeDSL.invoke() {
            factory<List<AbstractRectifier>>(Name.RECTIFIERS_SCOPE) {
                listOf()
            }
        }
    }
}
