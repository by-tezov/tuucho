package com.tezov.tuucho.core.domain.business.protocol

interface SourceIdentifierProtocol {

    fun accept(other: SourceIdentifierProtocol): Boolean

}