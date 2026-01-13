package com.tezov.tuucho.core.domain.business.mock

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope

class CoroutineTestScope {
    lateinit var mock: CoroutineScopesProtocol
        private set

    fun setup() {
        mock<CoroutineScopesProtocol>().also { mock = it }
    }

    private fun createMockContext(
        currentScope: CoroutineScope
    ) = mock<CoroutineContextProtocol> {
        everySuspend {
            await(
                block = any<suspend CoroutineScope.() -> Any?>()
            )
        } calls { args ->
            val block = args.arg(0) as suspend CoroutineScope.() -> Any?
            block(currentScope)
        }

        every {
            async(
                throwOnFailure = true,
                block = any<suspend CoroutineScope.() -> Any?>()
            )
        } calls { args ->
            val block = args.arg(1) as suspend CoroutineScope.() -> Any?
            val deferred = CompletableDeferred<Any?>()
            currentScope.launch {
                val result = block(currentScope)
                deferred.complete(result)
            }
            deferred
        }
    }

    private fun attach(
        scheduler: TestCoroutineScheduler
    ) {
        val ioDispatcher = StandardTestDispatcher(scheduler)
        val ioScope = CoroutineScope(ioDispatcher)

        val defaultDispatcher = StandardTestDispatcher(scheduler)
        val defaultScope = CoroutineScope(defaultDispatcher)

        val mainDispatcher = StandardTestDispatcher(scheduler)
        val mainScope = CoroutineScope(mainDispatcher)

        mock.apply {
            every { database } returns createMockContext(ioScope)
            every { network } returns createMockContext(ioScope)
            every { parser } returns createMockContext(defaultScope)
            every { renderer } returns createMockContext(defaultScope)
            every { navigation } returns createMockContext(defaultScope)
            every { event } returns createMockContext(defaultScope)
            every { useCase } returns createMockContext(defaultScope)
            every { action } returns createMockContext(defaultScope)

            every { default } returns createMockContext(defaultScope)
            every { main } returns createMockContext(mainScope)
            every { io } returns createMockContext(ioScope)
        }
    }

    fun run(
        body: suspend TestScope.() -> Unit
    ) = kotlinx.coroutines.test.runTest {
        attach(testScheduler)
        body()
    }

    fun verifyNoMoreCalls() {
        dev.mokkery.verifyNoMoreCalls(
            mock.database,
            mock.network,
            mock.parser,
            mock.renderer,
            mock.navigation,
            mock.useCase,
            mock.action,
            mock.event,
            mock.default,
            mock.main,
            mock.io,
        )
    }

    fun resetCalls() {
        dev.mokkery.resetCalls(
            mock.database,
            mock.network,
            mock.parser,
            mock.renderer,
            mock.navigation,
            mock.useCase,
            mock.action,
            mock.event,
            mock.default,
            mock.main,
            mock.io,
        )
    }
}
