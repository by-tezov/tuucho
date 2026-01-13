package com.tezov.tuucho.core.data.repository.assets

import okio.Source

interface AssetsProtocol {
    fun readFile(
        path: String
    ): Source
}
