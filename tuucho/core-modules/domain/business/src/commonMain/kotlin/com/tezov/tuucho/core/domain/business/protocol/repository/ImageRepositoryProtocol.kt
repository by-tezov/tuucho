package com.tezov.tuucho.core.domain.business.protocol.repository

object ImageRepositoryProtocol {
    interface Image<S : Any> {
        val source: S
        val size: Long
        val width: Int
        val height: Int
    }

    interface Remote {
        suspend fun <S : Any> process(
            target: String
        ): Image<S>
    }

    interface Local {
        suspend fun <S : Any> process(
            target: String
        ): Image<S>
    }
}
