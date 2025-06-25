package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.domain.model.ConfigModelDomain

fun ConfigModelDomain.Preload.Item.toAdapterConfig(
    isShared: Boolean
) = ExtraDataBreaker(
    url = url,
    version = version,
    isShared = isShared
)