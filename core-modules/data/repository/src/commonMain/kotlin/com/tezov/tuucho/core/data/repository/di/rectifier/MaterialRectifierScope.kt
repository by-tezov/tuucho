package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierIdGenerator
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.color.ColorsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.content.ContentsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension.DimensionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.form.FormValidatorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.option.OptionAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.option.OptionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.option.OptionsRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component.SettingAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component.SettingComponentNavigationRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.setting.component.SettingComponentRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.state.StateAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.state.StateRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.state.StatesRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.style.StyleAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.style.StyleRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.style.StylesRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextsRectifier
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

internal object MaterialRectifierScope {
    fun invoke() = scope(ScopeContext.Material) {
        factory<Scope> { this }
        rectifiers()

        idAssociation()
        settingAssociation()
        componentAssociation()
        contentAssociation()
        styleAssociation()
        optionAssociation()
        stateAssociation()
        textAssociation()
        colorAssociation()
        dimensionAssociation()
        actionAssociation()
    }

    private fun ScopeDSL.rectifiers() {
        factoryOf(::RectifierIdGenerator)
        factoryOf(::IdRectifier)
        factoryOf(::SettingComponentRectifier)
        factoryOf(::ComponentsRectifier)
        factoryOf(::ComponentRectifier)
        factoryOf(::ContentsRectifier)
        factoryOf(::ContentRectifier)
        factoryOf(::StylesRectifier)
        factoryOf(::StyleRectifier)
        factoryOf(::OptionsRectifier)
        factoryOf(::OptionRectifier)
        factoryOf(::StatesRectifier)
        factoryOf(::StateRectifier)
        factoryOf(::TextsRectifier)
        factoryOf(::TextRectifier)
        factoryOf(::ColorsRectifier)
        factoryOf(::ColorRectifier)
        factoryOf(::DimensionsRectifier)
        factoryOf(::DimensionRectifier)
        factoryOf(::ActionsRectifier)
        factoryOf(::ActionRectifier)
        factoryOf(::FormValidatorRectifier)

        associate<MaterialAssociation.Rectifier> {
            declaration<ComponentsRectifier>()
            declaration<ContentsRectifier>()
            declaration<StylesRectifier>()
            declaration<OptionsRectifier>()
            declaration<StatesRectifier>()
            declaration<TextsRectifier>()
            declaration<ColorsRectifier>()
            declaration<DimensionsRectifier>()
            declaration<ActionsRectifier>()
        }
    }

    private fun ScopeDSL.idAssociation() {
        factoryOf(::IdMatcher) associate IdAssociation.Matcher::class
    }

    private fun ScopeDSL.settingAssociation() {
        factoryOf(::SettingComponentNavigationRectifier) associate SettingAssociation.Rectifier::class
        declaration<IdRectifier>() associate SettingAssociation.Rectifier::class
    }

    private fun ScopeDSL.componentAssociation() {
        associate<ComponentAssociation.Rectifier> {
            declaration<IdRectifier>()
            declaration<SettingComponentRectifier>()
            declaration<ContentRectifier>()
            declaration<StyleRectifier>()
            declaration<OptionRectifier>()
            declaration<StateRectifier>()
        }
    }

    private fun ScopeDSL.contentAssociation() {
        associate<ContentAssociation.Rectifier> {
            declaration<IdRectifier>()
            declaration<ActionRectifier>()
            declaration<TextRectifier>()
        }
    }

    private fun ScopeDSL.styleAssociation() {
        associate<StyleAssociation.Rectifier> {
            declaration<IdRectifier>()
            declaration<DimensionRectifier>()
            declaration<ColorRectifier>()
        }
    }

    private fun ScopeDSL.optionAssociation() {
        associate<OptionAssociation.Rectifier> {
            declaration<IdRectifier>()
            declaration<FormValidatorRectifier>()
        }
    }

    private fun ScopeDSL.stateAssociation() {
        associate<StateAssociation.Rectifier> {
            declaration<IdRectifier>()
            declaration<TextRectifier>()
        }
    }

    private fun ScopeDSL.textAssociation() {
        declaration<IdRectifier>() associate TextAssociation.Rectifier::class
    }

    private fun ScopeDSL.colorAssociation() {
        declaration<IdRectifier>() associate ColorAssociation.Rectifier::class
    }

    private fun ScopeDSL.dimensionAssociation() {
        declaration<IdRectifier>() associate DimensionAssociation.Rectifier::class
    }

    private fun ScopeDSL.actionAssociation() {
        declaration<IdRectifier>() associate ActionAssociation.Rectifier::class
    }
}
