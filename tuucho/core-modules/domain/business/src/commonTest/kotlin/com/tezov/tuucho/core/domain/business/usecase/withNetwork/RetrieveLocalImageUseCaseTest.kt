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

class RetrieveLocalImageUseCaseTest {
    private lateinit var imagesRepository: ImageRepositoryProtocol.Local
    private lateinit var sut: RetrieveLocalImageUseCase

    @BeforeTest
    fun setup() {
        imagesRepository = mock()

        sut = RetrieveLocalImageUseCase(
            imagesRepository = imagesRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(imagesRepository)
    }

    @Test
    fun `invoke retrieves image from local repository and wraps it into Output`() = runTest {
        val url = "file://image.png"
        val repositoryImage = mock<ImageRepositoryProtocol.Image<Any>>()

        everySuspend {
            imagesRepository.process<Any>(url)
        } returns repositoryImage

        val result = sut.invoke(
            RetrieveLocalImageUseCase.Input(url = url)
        )

        assertSame(repositoryImage, result.image)

        verifySuspend {
            imagesRepository.process<Any>(url)
        }
    }
}
