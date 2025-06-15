package com.tezov.tuucho.core.data.proxy.repository

import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.domain.model._system.ConfigModelDomain

fun ConfigModelDomain.Preload.Item.toAdapterConfig(
    isShared: Boolean
) = ExtraDataBreaker(
    url = url,
    version = version,
    isShared = isShared
)