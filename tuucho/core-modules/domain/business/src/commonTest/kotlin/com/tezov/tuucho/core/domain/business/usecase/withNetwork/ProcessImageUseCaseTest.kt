package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ImageExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

class ProcessImageUseCaseTest {
    private lateinit var imageExecutor: ImageExecutorProtocol
    private lateinit var sut: ProcessImageUseCase

    @BeforeTest
    fun setup() {
        imageExecutor = mock()
        sut = ProcessImageUseCase(
            imageExecutor = imageExecutor
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(imageExecutor)
    }

    @Test
    fun `invoke executes executor for Image input`() = runTest {
        val imageModel = ImageModelDomain.from(
            command = "remote",
            target = "https://image.png"
        )

        val input = ProcessImageUseCase.Input.Image(
            image = imageModel,
            imageObjectOriginal = JsonObject(emptyMap())
        )

        val repositoryImage = mock<ImageRepositoryProtocol.Image<*>>()
        val repositoryFlow = flowOf(repositoryImage)

        everySuspend {
            imageExecutor.process(any())
        } returns repositoryFlow

        val result = sut.invoke(input)?.first()

        assertSame(repositoryImage, result)

        verifySuspend {
            @Suppress("UnusedFlow")
            imageExecutor.process(input = input)
        }
    }

    @Test
    fun `invoke executes executor for ImageObject input`() = runTest {
        val input = ProcessImageUseCase.Input.ImageObject(
            imageObject = JsonObject(emptyMap())
        )

        val repositoryImage = mock<ImageRepositoryProtocol.Image<*>>()
        val repositoryFlow = flowOf(repositoryImage)

        everySuspend {
            imageExecutor.process(any())
        } returns repositoryFlow

        val result = sut.invoke(input)?.first()

        assertSame(repositoryImage, result)

        verifySuspend {
            @Suppress("UnusedFlow")
            imageExecutor.process(input = input)
        }
    }
}
