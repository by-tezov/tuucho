@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import java.util.Locale

class SystemPlatformInformationAndroid : SystemPlatformInformationProtocol {
    override fun currentThreadName(): String = Thread.currentThread().name

    override fun currentLanguage(): String = Locale.getDefault().language

    override fun currentCountry(): String = Locale.getDefault().country
}
