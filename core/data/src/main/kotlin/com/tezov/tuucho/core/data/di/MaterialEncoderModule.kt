package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.parser.SchemaDataEncoder
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.encoder.ComponentSchemaDataEncoder
import com.tezov.tuucho.core.data.parser.encoder.ContentSchemaDataEncoder
import com.tezov.tuucho.core.data.parser.encoder.MaterialSchemaDataEncoder
import com.tezov.tuucho.core.data.parser.encoder.OptionSchemaDataEncoder
import com.tezov.tuucho.core.data.parser.encoder.StyleSchemaDataEncoder
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialEncoderModule {

    object Name {
        object Processor {
            val COMPONENT = named("MaterialEncoderModule.Name.Processor.COMPONENT")
            val STYLE = named("MaterialEncoderModule.Name.Processor.STYLE")
            val OPTION = named("MaterialEncoderModule.Name.Processor.OPTION")
            val CONTENT = named("MaterialEncoderModule.Name.Processor.CONTENT")
            val TEXT = named("MaterialEncoderModule.Name.Processor.TEXT")
            val COLOR = named("MaterialEncoderModule.Name.Processor.COLOR")
            val DIMENSION = named("MaterialEncoderModule.Name.Processor.DIMENSION")
        }

        object Matcher {
            val ID = named("MaterialEncoderModule.Name.Matcher.ID")
            val COMPONENT = named("MaterialEncoderModule.Name.Matcher.COMPONENT")
            val STYLE = named("MaterialEncoderModule.Name.Matcher.STYLE")
            val OPTION = named("MaterialEncoderModule.Name.Matcher.OPTION")
            val CONTENT = named("MaterialEncoderModule.Name.Matcher.CONTENT")
            val TEXT = named("MaterialEncoderModule.Name.Matcher.TEXT")
            val COLOR = named("MaterialEncoderModule.Name.Matcher.COLOR")
            val DIMENSION = named("MaterialEncoderModule.Name.Matcher.DIMENSION")
        }
    }

    internal operator fun invoke() = module {

        single<MaterialSchemaDataEncoder> {
            MaterialSchemaDataEncoder()
        }

        // Component
        single<ComponentSchemaDataEncoder> {
            ComponentSchemaDataEncoder
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.COMPONENT) {
            listOf(
                //LayoutLinearComponentSchemaDataMatcher
            )
        }

        single<List<SchemaDataEncoder>>(Name.Processor.COMPONENT) {
            listOf(
                get<StyleSchemaDataEncoder>(),
                get<OptionSchemaDataEncoder>(),
                get<ContentSchemaDataEncoder>(),
            )
        }

        // Content
        single<ContentSchemaDataEncoder> {
            ContentSchemaDataEncoder
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.CONTENT) {
            emptyList()
        }

        single<List<SchemaDataEncoder>>(Name.Processor.CONTENT) {
            listOf(
//                get<ComponentSchemaDataRectifier>(),
//                get<TextSchemaDataRectifier>(),
            )
        }

        // Style
        single<StyleSchemaDataEncoder> {
            StyleSchemaDataEncoder
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.STYLE) {
            emptyList()
        }

        single<List<SchemaDataEncoder>>(Name.Processor.STYLE) {
            emptyList()
        }

        // Option
        single<OptionSchemaDataEncoder> {
            OptionSchemaDataEncoder
        }

        single<List<SchemaDataMatcher>>(Name.Matcher.OPTION) {
            emptyList()
        }

        single<List<SchemaDataEncoder>>(Name.Processor.OPTION) {
            emptyList()
        }

    }
}


