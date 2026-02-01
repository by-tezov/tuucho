package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.mock.MockMiddlewareNext
import com.tezov.tuucho.core.domain.business.mock.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StoreActionMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var saveUseCase: SaveKeyValueToStoreUseCase
    private lateinit var removeUseCase: RemoveKeyValueFromStoreUseCase
    private lateinit var sut: StoreActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        saveUseCase = mock()
        removeUseCase = mock()
        sut = StoreActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            saveKeyValueToStore = saveUseCase,
            removeKeyValueFromStore = removeUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            saveUseCase,
            removeUseCase
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddlewareProtocol.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only store key value with query`() {
        val valid = ActionModel.from("store://key-value/save?a=1")
        val missingQuery = ActionModel.from("store://key-value/save")
        val wrongCmd = ActionModel.from("x://key-value/t?a=1")
        val wrongAuth = ActionModel.from("store://xxx/t?a=1")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, missingQuery))
        assertFalse(sut.accept(null, wrongCmd))
        assertFalse(sut.accept(null, wrongAuth))
    }

    @Test
    fun `process save calls SaveKeyValueToStoreUseCase for each entry then next`() = runTest {
        val action = ActionModel.from("store://key-value/save?a=1&b=2")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)
        everySuspend { useCaseExecutor.await<SaveKeyValueToStoreUseCase.Input, Unit>(any(), any()) } returns Unit

        flow { sut.run { process(context, next) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = saveUseCase,
                input = SaveKeyValueToStoreUseCase.Input("a".toKey(), "1".toValue())
            )
            useCaseExecutor.await(
                useCase = saveUseCase,
                input = SaveKeyValueToStoreUseCase.Input("b".toKey(), "2".toValue())
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process remove handles array of keys`() = runTest {
        val action = ActionModel.from("store://key-value/remove?x,y")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)
        everySuspend { useCaseExecutor.await<RemoveKeyValueFromStoreUseCase.Input, Unit>(any(), any()) } returns Unit

        flow { sut.run { process(context, next) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = removeUseCase,
                input = RemoveKeyValueFromStoreUseCase.Input("x".toKey())
            )
            useCaseExecutor.await(
                useCase = removeUseCase,
                input = RemoveKeyValueFromStoreUseCase.Input("y".toKey())
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process remove handles primitive key`() = runTest {
        val action = ActionModel.from("store://key-value/remove?z")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                models = listOf(action),
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)
        everySuspend { useCaseExecutor.await<RemoveKeyValueFromStoreUseCase.Input, Unit>(any(), any()) } returns Unit

        flow { sut.run { process(context, next) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = removeUseCase,
                input = RemoveKeyValueFromStoreUseCase.Input("z".toKey())
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process throws for unknown target`() = runTest {
        val action = ActionModel.from("store://key-value/xxx?a=1")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        assertFailsWith<DomainException> {
            flow { sut.run { process(context, null) } }.collect()
        }
    }

    @Test
    fun `process throws when query is null`() = runTest {
        val action = ActionModel.from("store://key-value/save")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        assertFailsWith<DomainException> {
            flow { sut.run { process(context, null) } }.collect()
        }
    }
}
