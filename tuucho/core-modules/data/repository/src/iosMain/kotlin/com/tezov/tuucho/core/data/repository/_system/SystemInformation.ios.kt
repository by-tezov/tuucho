@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import platform.Foundation.NSThread

class SystemInformationIos : SystemInformation.PlatformProtocol {
    override fun currentThreadName(): String {
        val name = NSThread.currentThread().name
        return if (NSThread.isMainThread()) {
            "main"
        } else {
            name ?: "background"
        }
    }
}
