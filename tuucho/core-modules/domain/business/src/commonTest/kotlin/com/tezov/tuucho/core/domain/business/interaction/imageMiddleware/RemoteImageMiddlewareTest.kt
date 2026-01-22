package com.tezov.tuucho.core.domain.business.interaction.imageMiddleware

import com.tezov.tuucho.core.domain.business.interaction.imageProcessor.RemoteImageMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.model.image.RemoteImage
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveRemoteImageUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RemoteImageMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var retrieveRemoteImage: RetrieveRemoteImageUseCase
    private lateinit var sut: RemoteImageMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        retrieveRemoteImage = mock()

        sut = RemoteImageMiddleware(
            useCaseExecutor = useCaseExecutor,
            retrieveRemoteImage = retrieveRemoteImage
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            retrieveRemoteImage
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(sut.priority, ImageMiddleware.Priority.DEFAULT)
    }

    @Test
    fun `accept matches only remote image command`() {
        val accepted = ImageModelDomain.from(
            command = RemoteImage.command,
            target = "https://image.png"
        )
        val rejected = ImageModelDomain.from(
            command = "local",
            target = "file.png"
        )
        assertTrue(sut.accept(accepted))
        assertFalse(sut.accept(rejected))
    }

    @Test
    fun `process returns current flow when next is null`() = runTest {
        val image = ImageModelDomain.from(RemoteImage.command, "https://image.png")
        val flow = flowOf(mock<ImageRepositoryProtocol.Image<*>>())

        everySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(url = image.target)
            )
        } returns flow

        val context = ImageMiddleware.Context(input = ProcessImageUseCase.Input.Image(image))
        val result = sut.process(context, next = null)
        assertSame(flow, result)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(url = image.target)
            )
        }
    }

    @Test
    fun `process returns null when both current and next are null`() = runTest {
        val image = ImageModelDomain.from(RemoteImage.command, "https://image.png")

        everySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(url = image.target)
            )
        } returns null

        val next = mock<MiddlewareProtocol.Next<ImageMiddleware.Context, Flow<ImageRepositoryProtocol.Image<*>>>>()
        everySuspend { next.invoke(any()) } returns null

        val context = ImageMiddleware.Context(input = ProcessImageUseCase.Input.Image(image))
        val result = sut.process(context, next)
        assertNull(result)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(url = image.target)
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process merges current and next when both exist`() = runTest {
        val image = ImageModelDomain.from(RemoteImage.command, "https://image.png")
        val currentFlow = flowOf(mock<ImageRepositoryProtocol.Image<*>>())
        val nextFlow = flowOf(mock<ImageRepositoryProtocol.Image<*>>())

        everySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(url = image.target)
            )
        } returns currentFlow

        val next = mock<MiddlewareProtocol.Next<ImageMiddleware.Context, Flow<ImageRepositoryProtocol.Image<*>>>>()
        everySuspend { next.invoke(any()) } returns nextFlow

        val context = ImageMiddleware.Context(input = ProcessImageUseCase.Input.Image(image))
        val result = sut.process(context, next)
        assertTrue(result is Flow<*>)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(url = image.target)
            )
            next.invoke(context)
        }
    }
}
