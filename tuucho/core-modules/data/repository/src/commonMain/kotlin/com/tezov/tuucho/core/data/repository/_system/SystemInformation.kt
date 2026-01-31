@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol

class SystemInformation(
    private val platformRepository: PlatformProtocol
) : SystemInformationProtocol {
    interface PlatformProtocol {
        fun currentThreadName(): String
    }

    override fun currentThreadName() = platformRepository.currentThreadName()
}
