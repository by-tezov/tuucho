package com.tezov.tuucho.core.domain.business.protocol

import org.koin.core.module.Module

interface ModuleProtocol {
    interface Group

    val group: Group

    fun Module.declaration()
}
