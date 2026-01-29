@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

class ListStringDelegate(
    private val values: List<String>
) : List<String> by values {
    override fun toString() = values.toString()
}
