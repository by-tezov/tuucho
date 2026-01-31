package com.tezov.tuucho.core.domain.business.mock

import com.tezov.tuucho.core.domain.business.protocol.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope

class CoroutineTestScope {
    lateinit var scheduler: TestCoroutineScheduler
        private set
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

        every { scope } returns currentScope
        every { context } returns currentScope.coroutineContext
    }

    private fun attach(
        scheduler: TestCoroutineScheduler
    ) {
        this.scheduler = scheduler
        val ioDispatcher = StandardTestDispatcher(scheduler)
        val ioScope = CoroutineScope(ioDispatcher)

        val defaultDispatcher = StandardTestDispatcher(scheduler)
        val defaultScope = CoroutineScope(defaultDispatcher)

        val mainDispatcher = StandardTestDispatcher(scheduler)
        val mainScope = CoroutineScope(mainDispatcher)

        mock.apply {
            every { io } returns createMockContext(ioScope)
            every { io } returns createMockContext(ioScope)
            every { default } returns createMockContext(defaultScope)
            every { default } returns createMockContext(defaultScope)
            every { default } returns createMockContext(defaultScope)
            every { default } returns createMockContext(defaultScope)
            every { default } returns createMockContext(defaultScope)
            every { default } returns createMockContext(defaultScope)
            every { default } returns createMockContext(defaultScope)

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
            mock.io,
            mock.io,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.main,
            mock.io,
        )
    }

    fun resetCalls() {
        dev.mokkery.resetCalls(
            mock.io,
            mock.io,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.default,
            mock.main,
            mock.io,
        )
    }

    fun advanceUntilIdle() {
        scheduler.advanceUntilIdle()
    }
}
