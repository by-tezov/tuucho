package com.tezov.tuucho.core.data.repository.assets

import okio.Source

internal interface AssetsProtocol {
    fun readFile(
        path: String
    ): Source
}
