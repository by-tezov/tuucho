package com.tezov.tuucho.core.domain.business.protocol.repository

object ImageRepositoryProtocol {
    interface Image<T : Any, C : Any> {
        val source: T
        val size: Long
        val width: Int
        val height: Int

        fun draw(
            canvas: C
        )
    }

    interface Remote {
        suspend fun <T : Any, C : Any> process(
            target: String
        ): Image<T, C>
    }

    interface Local {
        suspend fun <T : Any, C : Any> process(
            target: String
        ): Image<T, C>
    }
}
