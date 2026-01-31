@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

class SystemInformationAndroid : SystemInformation.PlatformProtocol {
    override fun currentThreadName() = "${Thread.currentThread().name}"
}
