package com.tezov.tuucho.core.data.repository.assets

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source
import platform.Foundation.NSBundle
import com.tezov.tuucho.core.data.repository.exception.DataException

class AssetsIos : AssetsProtocol {
    override fun readFile(path: String): Source {
        val parts = "assets/files/$path".split("/")
        val filename = parts.last()
        val name = filename.substringBeforeLast(".")
        val ext = filename.substringAfterLast(".")
        val subdir = parts.dropLast(1).joinToString("/")
        val filePath = NSBundle.mainBundle.pathForResource(name, ext, subdir)
            ?: throw DataException.Default("Resource not found in framework: $path")
        return FileSystem.SYSTEM.source(filePath.toPath())
    }
}
