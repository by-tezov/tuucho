package com.tezov.tuucho.core.data.repository.mock

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

fun mockCoroutineScope(
    currentScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) = mock<CoroutineScopesProtocol> {
    every { database } returns mockCoroutineContext(
        currentScope
    )
    every { network } returns mockCoroutineContext(
        currentScope
    )
    every { parser } returns mockCoroutineContext(
        currentScope
    )
    every { renderer } returns mockCoroutineContext(
        currentScope
    )
    every { navigation } returns mockCoroutineContext(
        currentScope
    )
    every { event } returns mockCoroutineContext(
        currentScope
    )
    every { useCase } returns mockCoroutineContext(
        currentScope
    )

    every { action } returns mockCoroutineContext(
        currentScope
    )

    every { default } returns mockCoroutineContext(
        currentScope
    )
    every { main } returns mockCoroutineContext(
        currentScope
    )
    every { io } returns mockCoroutineContext(
        currentScope
    )
}
