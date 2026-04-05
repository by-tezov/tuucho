@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import com.tezov.tuucho.core.data.repository.exception.DataException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class SystemPlatformFileIos : SystemPlatformFileProtocol {
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

    fun assetPath(
        path: String
    ): String? {
        val (name, ext, subdir) = splitResourcePath("assets/files/$path")
        return NSBundle.mainBundle.pathForResource(name, ext, subdir)
    }

    private fun splitResourcePath(
        path: String
    ): Triple<String, String, String?> {
        val parts = path.split("/")
        val filename = parts.last()
        val name = filename.substringBeforeLast(".")
        val ext = filename.substringAfterLast(".", "")
        val subdir = parts.dropLast(1).joinToString("/").ifEmpty { null }
        return Triple(name, ext, subdir)
    }
}
