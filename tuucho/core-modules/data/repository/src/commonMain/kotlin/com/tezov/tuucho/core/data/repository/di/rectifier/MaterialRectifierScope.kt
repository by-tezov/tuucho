package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
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
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.form.FormValidatorRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.image.ImageRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.image.ImagesRectifier
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
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.Associate.declaration
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.scope
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.plugin.module.dsl.factory

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
        imageAssociation()
    }

    private fun ScopeDSL.rectifiers() {
        factory<RectifierIdGenerator>()
        factory<IdRectifier>()
        factory<SettingComponentRectifier>()
        factory<ComponentsRectifier>()
        factory<ComponentRectifier>()
        factory<ContentsRectifier>()
        factory<ContentRectifier>()
        factory<StylesRectifier>()
        factory<StyleRectifier>()
        factory<OptionsRectifier>()
        factory<OptionRectifier>()
        factory<StatesRectifier>()
        factory<StateRectifier>()
        factory<TextsRectifier>()
        factory<TextRectifier>()
        factory<ColorsRectifier>()
        factory<ColorRectifier>()
        factory<DimensionsRectifier>()
        factory<DimensionRectifier>()
        factory<ActionsRectifier>()
        factory<ActionRectifier>()
        factory<ImagesRectifier>()
        factory<ImageRectifier>()
        factory<FormValidatorRectifier>()

        associate<MaterialRectifier.Association.Processor> {
            declaration<ComponentsRectifier>()
            declaration<ContentsRectifier>()
            declaration<StylesRectifier>()
            declaration<OptionsRectifier>()
            declaration<StatesRectifier>()
            declaration<TextsRectifier>()
            declaration<ColorsRectifier>()
            declaration<DimensionsRectifier>()
            declaration<ActionsRectifier>()
            declaration<ImagesRectifier>()
        }
    }

    private fun ScopeDSL.idAssociation() {
        factory<IdMatcher>() associate IdRectifier.Association.Matcher::class
    }

    private fun ScopeDSL.settingAssociation() {
        factory<SettingComponentNavigationRectifier>() associate SettingComponentRectifier.Association.Processor::class
        declaration<IdRectifier>() associate SettingComponentRectifier.Association.Processor::class
    }

    private fun ScopeDSL.componentAssociation() {
        associate<ComponentRectifier.Association.Processor> {
            declaration<IdRectifier>()
            declaration<SettingComponentRectifier>()
            declaration<ContentRectifier>()
            declaration<StyleRectifier>()
            declaration<OptionRectifier>()
            declaration<StateRectifier>()
        }
    }

    private fun ScopeDSL.contentAssociation() {
        associate<ContentRectifier.Association.Processor> {
            declaration<IdRectifier>()
            declaration<ActionRectifier>()
            declaration<ImageRectifier>()
            declaration<TextRectifier>()
        }
    }

    private fun ScopeDSL.styleAssociation() {
        associate<StyleRectifier.Association.Processor> {
            declaration<IdRectifier>()
            declaration<DimensionRectifier>()
            declaration<ColorRectifier>()
        }
    }

    private fun ScopeDSL.optionAssociation() {
        associate<OptionRectifier.Association.Processor> {
            declaration<IdRectifier>()
            declaration<FormValidatorRectifier>()
        }
    }

    private fun ScopeDSL.stateAssociation() {
        associate<StateRectifier.Association.Processor> {
            declaration<IdRectifier>()
            declaration<TextRectifier>()
        }
    }

    private fun ScopeDSL.textAssociation() {
        declaration<IdRectifier>() associate TextRectifier.Association.Processor::class
    }

    private fun ScopeDSL.colorAssociation() {
        declaration<IdRectifier>() associate ColorRectifier.Association.Processor::class
    }

    private fun ScopeDSL.dimensionAssociation() {
        declaration<IdRectifier>() associate DimensionRectifier.Association.Processor::class
    }

    private fun ScopeDSL.actionAssociation() {
        declaration<IdRectifier>() associate ActionRectifier.Association.Processor::class
    }

    private fun ScopeDSL.imageAssociation() {
        declaration<IdRectifier>() associate ImageRectifier.Association.Processor::class
    }
}
