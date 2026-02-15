@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import android.content.Context
import okio.FileSystem
import okio.Path.Companion.toPath

class SystemPlatformFileAndroid(
    private val context: Context
) : SystemPlatformFileProtocol {
    override fun fileSystem() = FileSystem.SYSTEM

    override fun pathFromCacheFolder(
        relativePath: String
    ) = "${context.cacheDir.path}/$relativePath".toPath()
}
