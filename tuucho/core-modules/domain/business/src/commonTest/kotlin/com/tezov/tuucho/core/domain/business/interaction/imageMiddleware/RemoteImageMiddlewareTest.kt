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
        assertEquals(ImageMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only remote image command`() {
        val accepted = ImageModelDomain.from(
            command = RemoteImage.command,
            target = "https://image.png"
        )

        val rejected = ImageModelDomain.from(
            command = "local",
            target = "image.png"
        )

        assertTrue(sut.accept(accepted))
        assertFalse(sut.accept(rejected))
    }

    @Test
    fun `process returns Element when remote image is found`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = RemoteImage.command,
            target = "https://image.png"
        )

        val repositoryImage = mock<ImageRepositoryProtocol.Image<*>>()

        everySuspend {
            useCaseExecutor.await<RetrieveRemoteImageUseCase.Input, RetrieveRemoteImageUseCase.Output>(
                any(),
                any()
            )
        } returns RetrieveRemoteImageUseCase.Output(
            image = repositoryImage
        )

        val context = ImageMiddleware.Context(
            input = ProcessImageUseCase.Input.Image(imageModel)
        )

        val result = sut.process(context, null)

        val element = result as ProcessImageUseCase.Output.Element
        assertSame(repositoryImage, element.image)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(
                    url = imageModel.target
                )
            )
        }
    }

    @Test
    fun `process calls next when remote image is not found`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = RemoteImage.command,
            target = "https://missing.png"
        )

        everySuspend {
            useCaseExecutor.await<RetrieveRemoteImageUseCase.Input, RetrieveRemoteImageUseCase.Output>(
                any(),
                any()
            )
        } returns null

        val context = ImageMiddleware.Context(
            input = ProcessImageUseCase.Input.Image(imageModel)
        )

        val next = mock<MiddlewareProtocol.Next<ImageMiddleware.Context, ProcessImageUseCase.Output>>()
        val expected = ProcessImageUseCase.Output.ElementArray(emptyList())

        everySuspend { next.invoke(any()) } returns expected

        val result = sut.process(context, next)

        assertSame(expected, result)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(
                    url = imageModel.target
                )
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process returns null when remote image not found and next is null`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = RemoteImage.command,
            target = "https://missing.png"
        )

        everySuspend {
            useCaseExecutor.await<RetrieveRemoteImageUseCase.Input, RetrieveRemoteImageUseCase.Output>(
                any(),
                any()
            )
        } returns null

        val context = ImageMiddleware.Context(
            input = ProcessImageUseCase.Input.Image(imageModel)
        )

        val result = sut.process(context, null)

        assertNull(result)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(
                    url = imageModel.target
                )
            )
        }
    }

    @Test
    fun `process does not call next when remote image is found`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = RemoteImage.command,
            target = "https://image.png"
        )

        val repositoryImage = mock<ImageRepositoryProtocol.Image<*>>()

        everySuspend {
            useCaseExecutor.await<RetrieveRemoteImageUseCase.Input, RetrieveRemoteImageUseCase.Output>(
                any(),
                any()
            )
        } returns RetrieveRemoteImageUseCase.Output(repositoryImage)

        val next = mock<MiddlewareProtocol.Next<ImageMiddleware.Context, ProcessImageUseCase.Output>>()

        val context = ImageMiddleware.Context(
            input = ProcessImageUseCase.Input.Image(imageModel)
        )

        sut.process(context, next)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveRemoteImage,
                input = RetrieveRemoteImageUseCase.Input(
                    url = imageModel.target
                )
            )
        }
    }
}
