package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._system.IdGenerator
import com.tezov.tuucho.core.data.parser.rectifier.ComponentSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.ContentSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.MaterialSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.OptionSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.StyleSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier._elements.button.TextButtonContentSchemaDataMatcher
import com.tezov.tuucho.core.data.parser.rectifier._elements.label.TextLabelContentSchemaDataMatcher
import com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear.LayoutLinearComponentSchemaDataMatcher
import com.tezov.tuucho.core.data.parser.rectifier._elements.layout.linear.LayoutLinearContentSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.colors.ColorsSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.dimensions.DimensionsSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.id.IdSchemaDataMatcher
import com.tezov.tuucho.core.data.parser.rectifier.id.IdSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextSchemaDataRectifier
import com.tezov.tuucho.core.data.parser.rectifier.texts.TextsSchemaDataRectifier
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

        single<MaterialSchemaDataRectifier> {
            MaterialSchemaDataRectifier()
        }

        // Id
        single<IdSchemaDataRectifier> {
            IdSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.ID) {
            IdSchemaDataMatcher.lists
        }

        // Component
        single<ComponentSchemaDataRectifier> {
            ComponentSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.COMPONENT) {
            listOf(
                LayoutLinearComponentSchemaDataMatcher
            )
        }

        single<List<SchemaDataRectifier>>(Name.Processor.COMPONENT) {
            listOf(
                get<IdSchemaDataRectifier>(),
                get<StyleSchemaDataRectifier>(),
                get<OptionSchemaDataRectifier>(),
                get<ContentSchemaDataRectifier>(),
                LayoutLinearContentSchemaDataRectifier
            )
        }

        // Content
        single<ContentSchemaDataRectifier> {
            ContentSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<SchemaDataRectifier>>(Name.Processor.CONTENT) {
            listOf(
                get<IdSchemaDataRectifier>(),
                get<ComponentSchemaDataRectifier>(),
                get<TextSchemaDataRectifier>(),
            )
        }

        // Style
        single<StyleSchemaDataRectifier> {
            StyleSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<SchemaDataRectifier>>(Name.Processor.STYLE) {
            listOf(
                get<IdSchemaDataRectifier>()
            )
        }

        // Option
        single<OptionSchemaDataRectifier> {
            OptionSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.OPTION) {
            emptyList()
        }

        single<List<SchemaDataRectifier>>(Name.Processor.OPTION) {
            listOf(
                get<IdSchemaDataRectifier>(),
            )
        }

        // Texts
        single<TextsSchemaDataRectifier> {
            TextsSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.TEXTS) {
            emptyList()
        }

        single<List<SchemaDataRectifier>>(Name.Processor.TEXTS) {
            listOf(
                get<TextSchemaDataRectifier>(),
            )
        }

        single<TextSchemaDataRectifier> {
            TextSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.TEXT) {
            listOf(
                TextLabelContentSchemaDataMatcher,
                TextButtonContentSchemaDataMatcher
            )
        }

        single<List<SchemaDataRectifier>>(Name.Processor.TEXT) {
            listOf(
                get<IdSchemaDataRectifier>(),
            )
        }

        // Colors
        single<ColorsSchemaDataRectifier> {
            ColorsSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.COLORS) {
            emptyList()
        }

        single<List<SchemaDataRectifier>>(Name.Processor.COLORS) {
            listOf(
                get<ColorSchemaDataRectifier>(),
            )
        }

        single<ColorSchemaDataRectifier> {
            ColorSchemaDataRectifier
        }

        single<List<SchemaDataRectifier>>(Name.Processor.COLOR) {
            listOf(
                get<IdSchemaDataRectifier>(),
            )
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.COLOR) {
            listOf(

            )
        }

        // Dimensions
        single<DimensionsSchemaDataRectifier> {
            DimensionsSchemaDataRectifier
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.DIMENSIONS) {
            emptyList()
        }

        single<List<SchemaDataRectifier>>(Name.Processor.DIMENSIONS) {
            listOf(
                get<DimensionSchemaDataRectifier>(),
            )
        }

        single<DimensionSchemaDataRectifier> {
            DimensionSchemaDataRectifier
        }

        single<List<SchemaDataRectifier>>(Name.Processor.DIMENSION) {
            listOf(
                get<IdSchemaDataRectifier>(),
            )
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.DIMENSION) {
            listOf(

            )
        }

    } //TODO clean as multiple private function


}


