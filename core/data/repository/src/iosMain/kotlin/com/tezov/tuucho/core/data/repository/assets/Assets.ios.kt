package com.tezov.tuucho.core.data.repository.assets

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source
import okio.buffer
import okio.use
import platform.Foundation.NSBundle

class AssetsIos : AssetsProtocol {
    override fun readFile(path: String): Source {
        val parts = "files/$path".split("/")
        val filename = parts.last()
        val name = filename.substringBeforeLast(".")
        val ext = filename.substringAfterLast(".")
        val subdir = parts.dropLast(1).joinToString("/")
//        val filePath = NSBundle.mainBundle.pathForResource(name, ext, subdir)
//            ?: error("Resource not found in framework: $path")

        val filePath = NSBundle.mainBundle.pathForResource("hello", "txt", "dummy")
            ?: error("Resource not found in framework: $path")

        println(FileSystem.SYSTEM.source(filePath.toPath()).buffer().use { it.readUtf8() })

        return FileSystem.SYSTEM.source(filePath.toPath())
    }
}