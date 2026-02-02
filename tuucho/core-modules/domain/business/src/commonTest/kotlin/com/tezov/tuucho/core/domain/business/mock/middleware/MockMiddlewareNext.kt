package com.tezov.tuucho.core.domain.business.mock.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock

interface SpyMiddlewareNext<C> {
    fun invoke(
        context: C
    )

    companion object {
        fun <C> create() = mock<SpyMiddlewareNext<C>>().apply {
            every { invoke(any()) } returns Unit
        }
    }
}

class MockMiddlewareNext<C>(
    var spy: SpyMiddlewareNext<C>? = null
) : MiddlewareProtocol.Next<C> {
    var invoke: MiddlewareProtocol.Next<C> = MiddlewareProtocol.Next {
        spy?.invoke(it)
    }

    override suspend fun invoke(
        context: C
    ) {
        invoke.run { invoke(context) }
    }
}
