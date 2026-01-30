@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import com.tezov.tuucho.core.data.repository.exception.DataException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class SystemPlatformIos : SystemPlatform {
    override fun fileSystem() = FileSystem.SYSTEM

    override fun pathFromCacheFolder(
        relativePath: String
    ): Path {
        val url = NSFileManager.defaultManager
            .URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
            .firstOrNull() as? NSURL
        url ?: throw DataException.Default("failed to access cache directory")
        val fullPath = url.path + "/" + relativePath
        return fullPath.toPath()
    }
}
