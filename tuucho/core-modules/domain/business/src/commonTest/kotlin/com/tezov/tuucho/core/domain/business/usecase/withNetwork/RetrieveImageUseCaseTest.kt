package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RetrieveImageUseCaseTest {

    private lateinit var imageRepository: ImageRepositoryProtocol
    private lateinit var sut: RetrieveImageUseCase<Any>

    @BeforeTest
    fun setup() {
        imageRepository = mock()
        sut = RetrieveImageUseCase(imageRepository = imageRepository)
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(imageRepository)
    }

    @Test
    fun `invoke returns Output flow from repository`() = runTest {
        val imageModel = ImageModel.from(
            value = "command://target",
            cacheKey = "key",
            tags = setOf("tag1"),
            tagsExcluder = setOf("exclude")
        )

        val repoImage = mock<Image<Any>>()
        val repoFlow = flowOf(repoImage)

        everySuspend { imageRepository.process<Any>(models = listOf(imageModel)) } returns repoFlow

        val input = RetrieveImageUseCase.Input.create(imageModel)
        val resultFlow = sut.invoke(input)

        resultFlow.collect { output ->
            assertEquals(repoImage, output.image)
        }

        verifySuspend {
            @Suppress("UnusedFlow")
            imageRepository.process<Any>(models = listOf(imageModel))
        }
    }

    @Test
    fun `Input create throws if cacheKey is missing`() = runTest {
        val jsonArray = buildJsonArray {
            add(buildJsonObject {
                put("source", "command://target") // no cacheKey
            })
        }

        val exception = assertFailsWith<DomainException.Default> {
            RetrieveImageUseCase.Input.create(jsonArray)
        }

        assertTrue(exception.message?.contains("should not be possible") == true)
    }

    @Test
    fun `Input create parses JsonArray into models`() = runTest {
        val jsonArray = buildJsonArray {
            add(buildJsonObject {
                put("source", "command://target1")
                put("cache-key", "cache1")
                put("tags", buildJsonArray { })
                put("tags-excluder", buildJsonArray { })
            })
            add(buildJsonObject {
                put("source", "command://target2")
                put("cache-key", "cache2")
                put("tags", buildJsonArray { })
                put("tags-excluder", buildJsonArray { })
            })
        }

        val input = RetrieveImageUseCase.Input.create(jsonArray)

        assertEquals(2, input.models.size)
        assertEquals("command://target1", input.models[0].toString())
        assertEquals("command://target2", input.models[1].toString())
    }

    @Test
    fun `invoke uses models from JsonArray input`() = runTest {
        val jsonArray = buildJsonArray {
            add(buildJsonObject {
                put("source", "command://target1")
                put("cache-key", "cache1")
                put("tags", buildJsonArray { })
                put("tags-excluder", buildJsonArray { })
            })
        }

        val input = RetrieveImageUseCase.Input.create(jsonArray)

        val repoImage: Image<Any> = mock()
        val repoFlow: Flow<Image<Any>> = flowOf(repoImage)

        everySuspend { imageRepository.process<Any>(models = input.models) } returns repoFlow

        val resultFlow = sut.invoke(input)
        resultFlow.collect { output ->
            assertEquals(repoImage, output.image)
        }

        verifySuspend {
            @Suppress("UnusedFlow")
            imageRepository.process<Any>(models = input.models)
        }
    }
}
