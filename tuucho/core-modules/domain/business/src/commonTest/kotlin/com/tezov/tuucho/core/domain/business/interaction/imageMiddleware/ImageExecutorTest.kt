package com.tezov.tuucho.core.domain.business.interaction.imageMiddleware

import com.tezov.tuucho.core.domain.business.interaction.imageProcessor.ImageExecutor
import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware.Context
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verify.VerifyMode.Companion.atLeast
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class ImageExecutorTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var middlewareExecutor: MiddlewareExecutorProtocol
    private lateinit var middlewareFirst: ImageMiddleware
    private lateinit var middlewareSecond: ImageMiddleware
    private lateinit var middlewareThird: ImageMiddleware

    private lateinit var sut: ImageExecutor

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        middlewareExecutor = mock()
        middlewareFirst = mock()
        middlewareSecond = mock()
        middlewareThird = mock()

        sut = ImageExecutor(
            coroutineScopes = coroutineTestScope.mock,
            middlewareExecutor = middlewareExecutor,
            middlewares = listOf(
                middlewareFirst,
                middlewareSecond,
                middlewareThird
            )
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            middlewareExecutor,
            middlewareFirst,
            middlewareSecond,
            middlewareThird
        )
    }

    @Test
    fun `process Image with no accepting middleware returns null`() = coroutineTestScope.run {
        val image = ImageModelDomain.from("image://test")

        every { middlewareFirst.accept(image) } returns false
        every { middlewareSecond.accept(image) } returns false
        every { middlewareThird.accept(image) } returns false

        val input = ProcessImageUseCase.Input.Image(image)

        val result = sut.process(input)
        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.image.await<Any>(any())
            middlewareFirst.accept(image)
            middlewareSecond.accept(image)
            middlewareThird.accept(image)
        }
    }

    @Test
    fun `process Image with one accepting middleware returns its output`() = coroutineTestScope.run {
        val image = ImageModelDomain.from("image://test")

        every { middlewareFirst.accept(image) } returns true
        every { middlewareSecond.accept(image) } returns false
        every { middlewareThird.accept(image) } returns false

        every { middlewareFirst.priority } returns ImageMiddleware.Priority.DEFAULT

        val expectedOutput = ProcessImageUseCase.Output.Element(mock())

        everySuspend {
            middlewareExecutor.process<Context, ProcessImageUseCase.Output>(
                any(),
                any()
            )
        } returns expectedOutput

        val input = ProcessImageUseCase.Input.Image(image)

        val result = sut.process(input)
        assertSame(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.image.await<Any>(any())
            middlewareFirst.accept(image)
            middlewareSecond.accept(image)
            middlewareThird.accept(image)
            middlewareExecutor.process<Context, ProcessImageUseCase.Output>(
                matches { it.size == 1 && it.first() == middlewareFirst },
                matches { it.input == input }
            )
        }
    }

    @Test
    fun `process Image calls accepting middlewares sorted by priority`() = coroutineTestScope.run {
        val image = ImageModelDomain.from("image://test")

        every { middlewareFirst.priority } returns ImageMiddleware.Priority.DEFAULT
        every { middlewareSecond.priority } returns ImageMiddleware.Priority.HIGH
        every { middlewareThird.priority } returns ImageMiddleware.Priority.LOW

        every { middlewareFirst.accept(image) } returns true
        every { middlewareSecond.accept(image) } returns true
        every { middlewareThird.accept(image) } returns true

        val output = ProcessImageUseCase.Output.Element(mock())

        everySuspend {
            middlewareExecutor.process<Context, ProcessImageUseCase.Output>(
                any(),
                any()
            )
        } returns output

        val input = ProcessImageUseCase.Input.Image(image)

        sut.process(input)

        verify(atLeast(2)) {
            middlewareFirst.priority
            middlewareSecond.priority
            middlewareThird.priority
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.image.await<Any>(any())
            middlewareFirst.accept(image)
            middlewareSecond.accept(image)
            middlewareThird.accept(image)
            middlewareExecutor.process<Context, ProcessImageUseCase.Output>(
                matches {
                    it.size == 3 &&
                        it[0] == middlewareSecond &&
                        it[1] == middlewareFirst &&
                        it[2] == middlewareThird
                },
                matches { it.input == input }
            )
        }
    }

    @Test
    fun `process ImageObject with no source returns null`() = coroutineTestScope.run {
        val imageObject = buildJsonObject { }

        val input = ProcessImageUseCase.Input.ImageObject(
            imageObject = imageObject
        )

        val result = sut.process(input)
        assertNull(result)
    }

    @Test
    fun `process ImageObject with source returns Element`() = coroutineTestScope.run {
        val imageSource = "image://source"
        val image = ImageModelDomain.from(imageSource)

        val imageObject = buildJsonObject {
            put("source", JsonPrimitive(imageSource))
        }

        every { middlewareFirst.accept(image) } returns true
        every { middlewareSecond.accept(image) } returns false
        every { middlewareThird.accept(image) } returns false

        every { middlewareFirst.priority } returns ImageMiddleware.Priority.DEFAULT

        val expectedOutput = ProcessImageUseCase.Output.Element(mock())

        everySuspend {
            middlewareExecutor.process<Context, ProcessImageUseCase.Output>(
                any(),
                any()
            )
        } returns expectedOutput

        val input = ProcessImageUseCase.Input.ImageObject(imageObject)

        val result = sut.process(input)
        assertSame(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.image.await<Any>(any())
            middlewareFirst.accept(image)
            middlewareSecond.accept(image)
            middlewareThird.accept(image)
            middlewareExecutor.process<Context, ProcessImageUseCase.Output>(
                matches { it.size == 1 && it.first() == middlewareFirst },
                any()
            )
        }
    }
}
