package com.tezov.tuucho.core.domain.business.jsonSchema._system

class SetStringDelegate(
    private val values: Set<String>
) : Set<String> by values {

    override fun toString() = values.toString()
}
