package com.tezov.tuucho.core.domain.business.protocol.repository

object ImageRepositoryProtocol {

    interface Image<T : Any, C : Any> {
        val source: T
        val width: Int
        val height: Int
        fun draw(canvas: C)

    }

    interface Remote {
        suspend fun <T : Any, C : Any> process(url: String): Image<T, C>
    }

}
