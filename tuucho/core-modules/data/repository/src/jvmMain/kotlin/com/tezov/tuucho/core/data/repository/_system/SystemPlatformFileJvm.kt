@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class SystemPlatformFileJvm : SystemPlatformFileProtocol {
    override fun fileSystem(): FileSystem = FileSystem.SYSTEM

    fun appFolder(): Path {
        val appDir = "${System.getProperty("user.home")}/.tuucho".toPath()
        fileSystem().createDirectories(appDir)
        return appDir
    }

    override fun pathFromCacheFolder(
        relativePath: String
    ): Path {
        val cacheDir = appFolder().resolve("cache".toPath())
        fileSystem().createDirectories(cacheDir)
        return cacheDir.resolve(relativePath.toPath())
    }

    fun classLoader() = Thread.currentThread().contextClassLoader

    fun assetPath(path: String): String = "files/$path"

}
