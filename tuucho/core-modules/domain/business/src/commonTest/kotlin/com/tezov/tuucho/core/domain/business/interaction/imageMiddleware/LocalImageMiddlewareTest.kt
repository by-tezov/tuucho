package com.tezov.tuucho.core.domain.business.interaction.imageMiddleware

import com.tezov.tuucho.core.domain.business.interaction.imageProcessor.LocalImageMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.model.image.LocalImage
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveLocalImageUseCase
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

class LocalImageMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var retrieveLocalImage: RetrieveLocalImageUseCase
    private lateinit var sut: LocalImageMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        retrieveLocalImage = mock()

        sut = LocalImageMiddleware(
            useCaseExecutor = useCaseExecutor,
            retrieveLocalImage = retrieveLocalImage
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            retrieveLocalImage
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ImageMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only local image command`() {
        val accepted = ImageModelDomain.from(
            command = LocalImage.command,
            target = "file.png"
        )

        val rejected = ImageModelDomain.from(
            command = "remote",
            target = "file.png"
        )

        assertTrue(sut.accept(accepted))
        assertFalse(sut.accept(rejected))
    }

    @Test
    fun `process returns Element when local image is found`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = LocalImage.command,
            target = "file.png"
        )

        val repositoryImage = mock<ImageRepositoryProtocol.Image<*>>()

        everySuspend {
            useCaseExecutor.await<RetrieveLocalImageUseCase.Input, RetrieveLocalImageUseCase.Output>(
                any(),
                any()
            )
        } returns RetrieveLocalImageUseCase.Output(
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
                useCase = retrieveLocalImage,
                input = RetrieveLocalImageUseCase.Input(
                    url = imageModel.target
                )
            )
        }
    }

    @Test
    fun `process calls next when local image is not found`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = LocalImage.command,
            target = "missing.png"
        )

        everySuspend {
            useCaseExecutor.await<RetrieveLocalImageUseCase.Input, RetrieveLocalImageUseCase.Output>(
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
                useCase = retrieveLocalImage,
                input = RetrieveLocalImageUseCase.Input(
                    url = imageModel.target
                )
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process returns null when local image not found and next is null`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = LocalImage.command,
            target = "missing.png"
        )

        everySuspend {
            useCaseExecutor.await<RetrieveLocalImageUseCase.Input, RetrieveLocalImageUseCase.Output>(
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
                useCase = retrieveLocalImage,
                input = RetrieveLocalImageUseCase.Input(
                    url = imageModel.target
                )
            )
        }
    }

    @Test
    fun `process does not call next when local image is found`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = LocalImage.command,
            target = "file.png"
        )

        val repositoryImage = mock<ImageRepositoryProtocol.Image<*>>()

        everySuspend {
            useCaseExecutor.await<RetrieveLocalImageUseCase.Input, RetrieveLocalImageUseCase.Output>(
                any(),
                any()
            )
        } returns RetrieveLocalImageUseCase.Output(repositoryImage)

        val next = mock<MiddlewareProtocol.Next<ImageMiddleware.Context, ProcessImageUseCase.Output>>()

        val context = ImageMiddleware.Context(
            input = ProcessImageUseCase.Input.Image(imageModel)
        )

        sut.process(context, next)

        verifySuspend {
            useCaseExecutor.await(
                useCase = retrieveLocalImage,
                input = RetrieveLocalImageUseCase.Input(
                    url = imageModel.target
                )
            )
        }
    }
}
