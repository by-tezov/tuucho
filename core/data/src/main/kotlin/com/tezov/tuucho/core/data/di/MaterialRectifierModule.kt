package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser._system.IdGenerator
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Rectifier
import com.tezov.tuucho.core.data.parser.rectifier.ComponentRectifier
import com.tezov.tuucho.core.data.parser.rectifier.ContentRectifier
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.parser.rectifier.OptionRectifier
import com.tezov.tuucho.core.data.parser.rectifier.StyleRectifier
import com.tezov.tuucho.core.data.parser.rectifier._elements.button.TextButtonContentMatcher
import com.tezov.tuucho.core.data.parser.rectifier._elements.label.TextLabelContentMatcher
import com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear.LayoutLinearComponentMatcher
import com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear.LayoutLinearContentRectifier
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorRectifier
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsRectifier
import com.tezov.tuucho.core.data.parser.rectifier.id.IdMatcher
import com.tezov.tuucho.core.data.parser.rectifier.id.IdRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsRectifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialRectifierModule {

    object Name {

        object Processor {
            val COMPONENT = named("MaterialRectifierModule.Name.Processor.COMPONENT")
            val STYLE = named("MaterialRectifierModule.Name.Processor.STYLE")
            val OPTION = named("MaterialRectifierModule.Name.Processor.OPTION")
            val CONTENT = named("MaterialRectifierModule.Name.Processor.CONTENT")
            val TEXTS = named("MaterialRectifierModule.Name.Processor.TEXTS")
            val TEXT = named("MaterialRectifierModule.Name.Processor.TEXT")
            val COLORS = named("MaterialRectifierModule.Name.Processor.COLORS")
            val COLOR = named("MaterialRectifierModule.Name.Processor.COLOR")
            val DIMENSIONS = named("MaterialRectifierModule.Name.Processor.DIMENSIONS")
            val DIMENSION = named("MaterialRectifierModule.Name.Processor.DIMENSION")
        }

        object Matcher {
            val ID = named("MaterialRectifierModule.Name.Matcher.ID")
            val COMPONENT = named("MaterialRectifierModule.Name.Matcher.COMPONENT")
            val STYLE = named("MaterialRectifierModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialRectifierModule.Name.Matcher.OPTION")
            val CONTENT = named("MaterialRectifierModule.Name.Matcher.CONTENT")
            val TEXTS = named("MaterialRectifierModule.Name.Matcher.TEXTS")
            val TEXT = named("MaterialRectifierModule.Name.Matcher.TEXT")
            val COLORS = named("MaterialRectifierModule.Name.Matcher.COLORS")
            val COLOR = named("MaterialRectifierModule.Name.Matcher.COLOR")
            val DIMENSIONS = named("MaterialRectifierModule.Name.Matcher.DIMENSIONS")
            val DIMENSION = named("MaterialRectifierModule.Name.Matcher.DIMENSION")
        }
    }

    internal operator fun invoke() = module {

        single<IdGenerator> {
            IdGenerator
        }

        single<MaterialRectifier> {
            MaterialRectifier()
        }

        // Id
        single<IdRectifier> {
            IdRectifier
        }

        single<List<Matcher>>(Name.Matcher.ID) {
            IdMatcher.lists
        }

        // Component
        single<ComponentRectifier> {
            ComponentRectifier
        }

        single<List<Matcher>>(Name.Matcher.COMPONENT) {
            listOf(
                LayoutLinearComponentMatcher
            )
        }

        single<List<Rectifier>>(Name.Processor.COMPONENT) {
            listOf(
                get<IdRectifier>(),
                get<StyleRectifier>(),
                get<OptionRectifier>(),
                get<ContentRectifier>(),
                LayoutLinearContentRectifier
            )
        }

        // Content
        single<ContentRectifier> {
            ContentRectifier
        }

        single<List<Matcher>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.CONTENT) {
            listOf(
                get<IdRectifier>(),
                get<ComponentRectifier>(),
                get<TextRectifier>(),
            )
        }

        // Style
        single<StyleRectifier> {
            StyleRectifier
        }

        single<List<Matcher>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.STYLE) {
            listOf(
                get<IdRectifier>()
            )
        }

        // Option
        single<OptionRectifier> {
            OptionRectifier
        }

        single<List<Matcher>>(Name.Matcher.OPTION) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.OPTION) {
            listOf(
                get<IdRectifier>(),
            )
        }

        // Texts
        single<TextsRectifier> {
            TextsRectifier
        }

        single<List<Matcher>>(Name.Matcher.TEXTS) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.TEXTS) {
            listOf(
                get<TextRectifier>(),
            )
        }

        single<TextRectifier> {
            TextRectifier
        }

        single<List<Matcher>>(Name.Matcher.TEXT) {
            listOf(
                TextLabelContentMatcher,
                TextButtonContentMatcher
            )
        }

        single<List<Rectifier>>(Name.Processor.TEXT) {
            listOf(
                get<IdRectifier>(),
            )
        }

        // Colors
        single<ColorsRectifier> {
            ColorsRectifier
        }

        single<List<Matcher>>(Name.Matcher.COLORS) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.COLORS) {
            listOf(
                get<ColorRectifier>(),
            )
        }

        single<ColorRectifier> {
            ColorRectifier
        }

        single<List<Rectifier>>(Name.Processor.COLOR) {
            listOf(
                get<IdRectifier>(),
            )
        }

        single<List<Matcher>>(Name.Matcher.COLOR) {
            emptyList()
        }

        // Dimensions
        single<DimensionsRectifier> {
            DimensionsRectifier
        }

        single<List<Matcher>>(Name.Matcher.DIMENSIONS) {
            emptyList()
        }

        single<List<Rectifier>>(Name.Processor.DIMENSIONS) {
            listOf(
                get<DimensionRectifier>(),
            )
        }

        single<DimensionRectifier> {
            DimensionRectifier
        }

        single<List<Rectifier>>(Name.Processor.DIMENSION) {
            listOf(
                get<IdRectifier>(),
            )
        }

        single<List<Matcher>>(Name.Matcher.DIMENSION) {
            emptyList()
        }

    } //TODO clean as multiple private function


}


