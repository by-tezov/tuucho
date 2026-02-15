package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransformImageJsonArrayToImageModelUseCaseTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var resolveLanguageValue: ResolveLanguageValueUseCase

    private lateinit var sut: TransformImageJsonArrayToImageModelUseCase

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        resolveLanguageValue = mock()
        sut = TransformImageJsonArrayToImageModelUseCase(
            useCaseExecutor = useCaseExecutor,
            resolveLanguageValue = resolveLanguageValue
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(useCaseExecutor, resolveLanguageValue)
    }

    @Test
    fun `Invoke throws if incorrect JsonArray`() = runTest {
        val jsonArray = buildJsonArray {
            add("not an object")
        }

        val exception = assertFailsWith<DomainException.Default> {
            sut.invoke(TransformImageJsonArrayToImageModelUseCase.Input(jsonArray))
        }

        assertTrue(exception.message?.contains("expect JsonObject") == true)
    }

    @Test
    fun `Invoke throws if id is missing`() = runTest {
        val jsonObject = buildJsonObject {
            put("default", "command://target") // no id
        }
        val jsonArray = buildJsonArray {
            add(jsonObject)
        }

        everySuspend {
            useCaseExecutor.await<ResolveLanguageValueUseCase.Input, ResolveLanguageValueUseCase.Output>(any(), any())
        } returns ResolveLanguageValueUseCase.Output(value = "command://target")

        val exception = assertFailsWith<DomainException.Default> {
            sut.invoke(TransformImageJsonArrayToImageModelUseCase.Input(jsonArray))
        }

        assertTrue(exception.message?.contains("should not be possible") == true)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = resolveLanguageValue,
                input = ResolveLanguageValueUseCase.Input(
                    resolvedKey = ImageSchema.Key.default,
                    jsonObject = jsonObject
                )
            )
        }
    }

    @Test
    fun `Invoke with 2 object return 2 models`() = runTest {
        val jsonObject1 = buildJsonObject {
            put("default", "command://target1")
            put("id", buildJsonObject { put("value", "id1") })
            put("tags", buildJsonArray { })
            put("tags-excluder", buildJsonArray { })
        }
        val jsonObject2 = buildJsonObject {
            put("default", "command://target2")
            put("id", buildJsonObject { put("value", "id2") })
            put("tags", buildJsonArray { })
            put("tags-excluder", buildJsonArray { })
        }

        val jsonArray = buildJsonArray {
            add(jsonObject1)
            add(jsonObject2)
        }

        everySuspend {
            useCaseExecutor.await<ResolveLanguageValueUseCase.Input, ResolveLanguageValueUseCase.Output>(any(), any())
        } sequentially {
            returns(ResolveLanguageValueUseCase.Output(value = "command://target1"))
            returns(ResolveLanguageValueUseCase.Output(value = "command://target2"))
        }

        val result = sut.invoke(TransformImageJsonArrayToImageModelUseCase.Input(jsonArray))

        assertEquals(2, result.models?.size)
        assertEquals("command://target1#id1#-tags:[]-tagsExcluder:[]", result.models?.get(0).toString())
        assertEquals("command://target2#id2#-tags:[]-tagsExcluder:[]", result.models?.get(1).toString())

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = resolveLanguageValue,
                input = ResolveLanguageValueUseCase.Input(
                    resolvedKey = ImageSchema.Key.default,
                    jsonObject = jsonObject1
                )
            )
            useCaseExecutor.await(
                useCase = resolveLanguageValue,
                input = ResolveLanguageValueUseCase.Input(
                    resolvedKey = ImageSchema.Key.default,
                    jsonObject = jsonObject2
                )
            )
        }
    }
}
