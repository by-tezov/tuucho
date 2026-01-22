package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifyNoMoreCalls
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
    fun `invoke retrieves image flow from local repository`() = runTest {
        val url = "file://image.png"
        val repositoryImage = mock<ImageRepositoryProtocol.Image<Any>>()
        val repositoryFlow = flowOf(repositoryImage)

        every {
            imagesRepository.process<Any>(url)
        } returns repositoryFlow

        val result = sut
            .invoke(
                RetrieveLocalImageUseCase.Input(url = url)
            ).first()

        assertSame(repositoryImage, result)

        verify {
            @Suppress("UnusedFlow")
            imagesRepository.process<Any>(url)
        }
    }
}
