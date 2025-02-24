package com.tezov.tuucho.core.data.cache.parser.decoder


interface ModelDomainDecoder<IN : Any, OUT : Any, CONFIG : DecoderConfig> {
    suspend fun decode(element: IN, config: CONFIG): OUT
}

typealias JsonEntityDecoderToModelDomain<INOUT> = ModelDomainDecoder<INOUT, INOUT, DecoderConfig>

typealias IdValueEntityDecoderToModelDomain<INOUT> = ModelDomainDecoder<INOUT, INOUT, DecoderConfig>