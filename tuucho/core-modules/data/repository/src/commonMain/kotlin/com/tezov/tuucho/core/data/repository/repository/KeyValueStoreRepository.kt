package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey

internal object KeyValueStoreRepository {
    val language = "language".toKey()
    val country = "country".toKey()
}
