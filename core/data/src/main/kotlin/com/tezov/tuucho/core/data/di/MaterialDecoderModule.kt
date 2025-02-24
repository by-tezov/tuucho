package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.cache.parser.decoder.ComponentModelDomainDecoder
import com.tezov.tuucho.core.data.cache.parser.decoder.ContentModelDomainDecoder
import com.tezov.tuucho.core.data.cache.parser.decoder.JsonEntityDecoderToModelDomain
import com.tezov.tuucho.core.data.cache.parser.decoder.MaterialModelDomainDecoder
import com.tezov.tuucho.core.data.cache.parser.decoder.OptionModelDomainDecoder
import com.tezov.tuucho.core.data.cache.parser.decoder.StyleModelDomainDecoder
import com.tezov.tuucho.core.data.cache.parser.decoder.TextModelDomainDecoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MaterialDecoderModule {

    object Name {
        object Decoder {
            val CONTENT_MAP = named("MaterialDecoderModule.Name.Decoder.CONTENT_MAP")
            val STYLE_MAP = named("MaterialDecoderModule.Name.Decoder.STYLE_MAP")
        }
    }

    internal operator fun invoke() = module {
        single<TextModelDomainDecoder> {
            TextModelDomainDecoder(
                database = get<Database>()
            )
        }

        // Style
        single<Map<String, JsonEntityDecoderToModelDomain<JsonElement>>>(
            Name.Decoder.STYLE_MAP
        ) {
            mapOf()
        }

        single<StyleModelDomainDecoder> {
            StyleModelDomainDecoder(
                database = get<Database>()
            )
        }
        //***

        single<OptionModelDomainDecoder> {
            OptionModelDomainDecoder(
                database = get<Database>()
            )
        }

        // Content
        single<Map<String, JsonEntityDecoderToModelDomain<JsonElement>>>(
            Name.Decoder.CONTENT_MAP
        ) {
            mapOf()
        }

        single<ContentModelDomainDecoder> {
            ContentModelDomainDecoder(
                database = get<Database>()
            )
        }
        //***

        single<ComponentModelDomainDecoder> {
            ComponentModelDomainDecoder(
                database = get<Database>()
            )
        }

        single<MaterialModelDomainDecoder> {
            MaterialModelDomainDecoder(
                database = get<Database>(),
                json = get<Json>()
            )
        }
    }


}


