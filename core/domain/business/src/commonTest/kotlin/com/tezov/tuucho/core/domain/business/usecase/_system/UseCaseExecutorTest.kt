package com.tezov.tuucho.core.domain.business.usecase._system

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.BeforeTest

class UseCaseExecutorTest {

    private lateinit var sut: UseCaseExecutor
    private lateinit var scopes: CoroutineScopesProtocol
    private lateinit var useCaseContext: CoroutineContextProtocol

    @BeforeTest
    fun setUp() {
        useCaseContext = mock<CoroutineContextProtocol>(MockMode.autoUnit)


        // TODO stub with every

        scopes = mock {
            every { useCase } returns useCaseContext
        }

        sut = UseCaseExecutor(scopes)
    }

    private class TestSyncUseCase : UseCaseProtocol.Sync<String, String> {
        override fun invoke(input: String): String = "Hello $input"
    }

    private class TestAsyncUseCase : UseCaseProtocol.Async<String, String> {
        override suspend fun invoke(input: String): String = "Hi $input"
    }

//    @Test
//    fun `invoke executes Sync use case and calls onResult`() {
//        var result: String? = null
//        sut.invoke(TestSyncUseCase(), "World") { result = this }
//        assertEquals("Hello World", result)
//    }
//
//    @Test
//    fun `invoke executes Async use case and calls onResult`() {
//        var result: String? = null
//        sut.invoke(TestAsyncUseCase(), "World") { result = this }
//        assertEquals("Hi World", result)
//    }
//
//    @Test
//    fun `invoke calls onException when DomainException is thrown`() {
//        val failingUseCase = object : UseCaseProtocol.Sync<String, String> {
//            override fun invoke(input: String): String = throw DomainException.Default("fail")
//        }
//
//        var exception: DomainException? = null
//        sut.invoke(failingUseCase, "X", onException = { exception = it })
//
//        assertTrue(exception is DomainException.Default)
//        assertEquals("fail", exception?.message)
//    }
//
//    @Test
//    fun `invoke wraps unknown exceptions into DomainException Unknown`() {
//        val failingUseCase = object : UseCaseProtocol.Sync<String, String> {
//            override fun invoke(input: String): String = throw IllegalStateException("boom")
//        }
//
//        var exception: DomainException? = null
//        sut.invoke(failingUseCase, "X", onException = { exception = it })
//
//        assertTrue(exception is DomainException.Unknown)
//        assertTrue(exception?.cause is IllegalStateException)
//    }
//
//    @Test
//    fun `invokeSuspend executes Sync use case and returns result`() = runBlocking {
//        val result = sut.invokeSuspend(TestSyncUseCase(), "World")
//        assertEquals("Hello World", result)
//    }
//
//    @Test
//    fun `invokeSuspend executes Async use case and returns result`() = runBlocking {
//        val result = sut.invokeSuspend(TestAsyncUseCase(), "World")
//        assertEquals("Hi World", result)
//    }
//
//    @Test
//    fun `invokeSuspend throws DomainException as is`() = runBlocking {
//        val useCase = object : UseCaseProtocol.Sync<String, String> {
//            override fun invoke(input: String): String = throw DomainException.Default("oops")
//        }
//
//        val ex = assertFailsWith<DomainException.Default> {
//            sut.invokeSuspend(useCase, "X")
//        }
//        assertEquals("oops", ex.message)
//    }
//
//    @Test
//    fun `invokeSuspend wraps unknown exceptions into DomainException Unknown`() = runBlocking {
//        val useCase = object : UseCaseProtocol.Sync<String, String> {
//            override fun invoke(input: String): String = throw IllegalArgumentException("bad")
//        }
//
//        val ex = assertFailsWith<DomainException.Unknown> {
//            sut.invokeSuspend(useCase, "X")
//        }
//        assertTrue(ex.cause is IllegalArgumentException)
//    }
}