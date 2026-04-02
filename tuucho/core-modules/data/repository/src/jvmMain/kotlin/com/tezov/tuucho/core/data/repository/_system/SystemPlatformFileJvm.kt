@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class SystemPlatformFileJvm : SystemPlatformFileProtocol {
    override fun fileSystem(): FileSystem = FileSystem.SYSTEM

    override fun pathFromCacheFolder(
        relativePath: String
    ): Path {
        val cacheDir = "cache".toPath()
        fileSystem().createDirectories(cacheDir)
        return cacheDir.resolve(relativePath.toPath())
    }
}
