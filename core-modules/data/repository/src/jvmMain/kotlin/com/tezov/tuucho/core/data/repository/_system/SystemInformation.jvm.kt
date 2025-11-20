package com.tezov.tuucho.core.data.repository._system

import com.tezov.tuucho.core.data.repository.repository.SystemInformation

class SystemInformationJvm: SystemInformation.PlatformProtocol {

    override fun currentThreadName() = "${Thread.currentThread().name}"

}
