package com.tezov.tuucho.core.data.repository._system

import com.tezov.tuucho.core.data.repository.repository.SystemInformation

class SystemInformationAndroid : SystemInformation.PlatformProtocol {

    override fun currentThreadName() = "${Thread.currentThread().name}"

}
