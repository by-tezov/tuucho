package com.tezov.tuucho.core.data.repository.repository

import platform.Foundation.NSThread

class SystemInformationIos: SystemRepository.PlatformProtocol {

    override fun currentThreadName(): String {
        val name = NSThread.currentThread().name
        return if (NSThread.isMainThread()) {
            "main"
        } else {
            name ?: "background"
        }
    }

}
