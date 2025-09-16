package com.tezov.tuucho.core.data.repository.assets

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source
import platform.Foundation.NSBundle

class AssetsIos : AssetsProtocol {
    override fun readFile(path: String): Source {
        val parts = "files/$path".split("/")
        val name = parts.last().substringBeforeLast(".")
        val ext = parts.last().substringAfterLast(".")
        val filePath = NSBundle.mainBundle.pathForResource(name, ext)
            ?: error("Resource not found: $path")
        return FileSystem.SYSTEM.source(filePath.toPath())
    }
}