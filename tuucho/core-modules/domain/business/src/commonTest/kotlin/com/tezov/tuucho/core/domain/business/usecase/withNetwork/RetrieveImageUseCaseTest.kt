package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.mock.middlewareWithReturn.MockMiddlewareExecutorWithReturn
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RetrieveImageUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var middlewareExecutor: MiddlewareExecutorProtocolWithReturn

    private lateinit var imageRepository: ImageRepositoryProtocol
    private lateinit var retrieveImageMiddlewares: List<RetrieveImageMiddleware<Any>>

    private lateinit var sut: RetrieveImageUseCase<Any>

    @BeforeTest
    fun setup() {
        imageRepository = mock()
        middlewareExecutor = MockMiddlewareExecutorWithReturn()
        coroutineTestScope.setup()
        retrieveImageMiddlewares = listOf()
        sut = RetrieveImageUseCase(
            coroutineScopes = coroutineTestScope.mock,
            imageRepository = imageRepository,
            middlewareExecutor = middlewareExecutor,
            retrieveImageMiddlewares = retrieveImageMiddlewares
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(imageRepository)
    }

    @Test
    fun `invoke returns Output flow from repository`() = coroutineTestScope.run {
        val imageModel = ImageModel.from(
            value = "command://target",
            id = "key",
            tags = setOf("tag1"),
            tagsExcluder = setOf("exclude")
        )

        val repoImage = mock<Image<Any>>()
        val repoFlow = flowOf(repoImage)

        everySuspend { imageRepository.process<Any>(models = listOf(imageModel)) } returns repoFlow

        val input = RetrieveImageUseCase.Input(listOf(imageModel))
        val resultFlow = sut.invoke(input)

        resultFlow.collect { output ->
            assertEquals(repoImage, output.image)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.withContext<Any>(any())
            coroutineTestScope.mock.io.dispatcher
            @Suppress("UnusedFlow")
            imageRepository.process<Any>(models = listOf(imageModel))
        }
    }
}
