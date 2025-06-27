package com.tezov.tuucho.core.domain.protocol

interface ActionHandlerProtocol {

    object Priority {
        val LOW = 0
        val DEFAULT = 100
        val HIGH = 200
    }

    val priority: Int

    fun accept(id: String, action: String): Boolean

    fun process(id: String, action: String): Boolean

}