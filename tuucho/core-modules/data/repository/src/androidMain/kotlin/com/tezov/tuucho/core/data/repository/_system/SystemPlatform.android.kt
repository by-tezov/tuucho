@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import android.content.Context
import okio.FileSystem
import okio.Path.Companion.toPath

class SystemPlatformAndroid(
    private val context: Context
) : SystemPlatform {
    override fun fileSystem() = FileSystem.SYSTEM

    override fun pathFromCacheFolder(
        relativePath: String
    ) = "${context.cacheDir.path}/$relativePath".toPath()
}
