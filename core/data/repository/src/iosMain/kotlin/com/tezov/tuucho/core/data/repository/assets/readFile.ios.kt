package com.tezov.tuucho.core.data.repository.assets

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

@ExperimentalForeignApi
actual fun readResourceFile(path: String): String {
    //TODO test
    val parts = "files/$path".split("/")
    val name = parts.last().substringBeforeLast(".")
    val ext = parts.last().substringAfterLast(".")
    val filePath = NSBundle.mainBundle.pathForResource(name, ext)
        ?: error("Resource not found: $path")
    return NSString.stringWithContentsOfFile(filePath, NSUTF8StringEncoding, null) as String
}