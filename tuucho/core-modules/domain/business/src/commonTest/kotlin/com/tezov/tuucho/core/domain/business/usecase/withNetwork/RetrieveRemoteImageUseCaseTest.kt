package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

class RetrieveRemoteImageUseCaseTest {
    private lateinit var imagesRepository: ImageRepositoryProtocol.Remote
    private lateinit var sut: RetrieveRemoteImageUseCase

    @BeforeTest
    fun setup() {
        imagesRepository = mock()

        sut = RetrieveRemoteImageUseCase(
            imagesRepository = imagesRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(imagesRepository)
    }

    @Test
    fun `invoke retrieves image from remote repository and wraps it into Output`() = runTest {
        val url = "https://image.png"
        val repositoryImage = mock<ImageRepositoryProtocol.Image<Any>>()

        everySuspend {
            imagesRepository.process<Any>(url)
        } returns repositoryImage

        val result = sut.invoke(
            RetrieveRemoteImageUseCase.Input(url = url)
        )

        assertSame(repositoryImage, result.image)

        verifySuspend {
            imagesRepository.process<Any>(url)
        }
    }
}
