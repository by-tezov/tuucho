package com.tezov.tuucho.core.domain.business.mock

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.FlowCollector

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

class MockMiddlewareNext<C, R : Any>(
    var spy: SpyMiddlewareNext<C>? = null
) : MiddlewareProtocol.Next<C, R> {
    var invoke: MiddlewareProtocol.Next<C, R> = MiddlewareProtocol.Next {
        spy?.invoke(it)
    }

    override suspend fun FlowCollector<R>.invoke(
        context: C
    ) {
        invoke.run { invoke(context) }
    }
}
