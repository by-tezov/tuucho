package com.tezov.tuucho.core.data.proxy.repository

import com.tezov.tuucho.core.data.parser.encoder.EncoderConfig
import com.tezov.tuucho.core.domain.model._system.ConfigModelDomain

fun ConfigModelDomain.Preload.Item.toAdapterConfig(
    isShared: Boolean
) = EncoderConfig(
    url = url,
    version = version,
    isShared = isShared
)