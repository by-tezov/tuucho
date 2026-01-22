package com.tezov.tuucho.core.domain.business.protocol.repository

import kotlinx.coroutines.flow.Flow

object ImageRepositoryProtocol {
    interface Image<S : Any> {
        val source: S
        val size: Long
        val width: Int
        val height: Int
    }

    interface Remote {
        fun <S : Any> process(
            target: String
        ): Flow<Image<S>>
    }

    interface Local {
        fun <S : Any> process(
            target: String
        ): Flow<Image<S>>
    }
}
