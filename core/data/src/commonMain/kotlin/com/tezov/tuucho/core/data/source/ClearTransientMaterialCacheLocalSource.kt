package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol

class ClearTransientMaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource
) {

    suspend fun process(
        urlOrigin: String
    ) {
        coroutineScopes.onDatabase {
            materialDatabaseSource.clearTransient(Lifetime.Transient(urlOrigin))
        }
    }
}