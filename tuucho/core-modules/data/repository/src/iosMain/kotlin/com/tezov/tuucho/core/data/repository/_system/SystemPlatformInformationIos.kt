@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import platform.Foundation.NSLocale
import platform.Foundation.NSThread
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

class SystemPlatformInformationIos : SystemPlatformInformationProtocol {
    override fun currentThreadName(): String {
        val name = NSThread.currentThread().name
        return if (NSThread.isMainThread()) {
            "main"
        } else {
            name ?: "background"
        }
    }

    override fun currentLanguage(): String = NSLocale.currentLocale.languageCode

    override fun currentCountry(): String? = NSLocale.currentLocale.countryCode
}
