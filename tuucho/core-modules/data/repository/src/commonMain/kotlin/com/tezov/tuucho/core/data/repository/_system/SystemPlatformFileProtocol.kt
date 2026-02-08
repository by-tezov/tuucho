@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import okio.FileSystem
import okio.Path

interface SystemPlatformFileProtocol {
    fun fileSystem(): FileSystem

    fun pathFromCacheFolder(
        relativePath: String
    ): Path
}
