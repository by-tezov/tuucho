package com.tezov.tuucho.core.data.repository.assets

import com.tezov.tuucho.core.data.repository.exception.DataException
import okio.Source
import okio.buffer
import okio.source

internal class AssetsJvm : AssetsProtocol {
    override fun readFile(
        path: String
    ): Source {
        val fullPath = "files/$path"
        val stream = this::class.java.classLoader
            ?.getResourceAsStream(fullPath)
            ?: throw DataException.Default("Resource not found in classpath: $fullPath")

        return stream.source().buffer()
    }
}
