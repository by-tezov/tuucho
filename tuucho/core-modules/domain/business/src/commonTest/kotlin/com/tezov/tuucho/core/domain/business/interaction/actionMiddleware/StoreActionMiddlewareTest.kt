package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
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
        assertEquals(ActionMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only store key value with query`() {
        val valid = ActionModelDomain.from("store://key-value/save?a=1")
        val missingQuery = ActionModelDomain.from("store://key-value/save")
        val wrongCmd = ActionModelDomain.from("x://key-value/t?a=1")
        val wrongAuth = ActionModelDomain.from("store://xxx/t?a=1")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, missingQuery))
        assertFalse(sut.accept(null, wrongCmd))
        assertFalse(sut.accept(null, wrongAuth))
    }

    @Test
    fun `process save calls SaveKeyValueToStoreUseCase for each entry then next`() = runTest {
        val action = ActionModelDomain.from("store://key-value/save?a=1&b=2")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { useCaseExecutor.await<SaveKeyValueToStoreUseCase.Input, Unit>(any(), any()) } returns Unit
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = saveUseCase,
                input = SaveKeyValueToStoreUseCase.Input("a".toKey(), "1".toValue())
            )
            useCaseExecutor.await(
                useCase = saveUseCase,
                input = SaveKeyValueToStoreUseCase.Input("b".toKey(), "2".toValue())
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process remove handles array of keys`() = runTest {
        val action = ActionModelDomain.from("store://key-value/remove?x,y")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { useCaseExecutor.await<RemoveKeyValueFromStoreUseCase.Input, Unit>(any(), any()) } returns Unit
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = removeUseCase,
                input = RemoveKeyValueFromStoreUseCase.Input("x".toKey())
            )
            useCaseExecutor.await(
                useCase = removeUseCase,
                input = RemoveKeyValueFromStoreUseCase.Input("y".toKey())
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process remove handles primitive key`() = runTest {
        val action = ActionModelDomain.from("store://key-value/remove?z")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        everySuspend { useCaseExecutor.await<RemoveKeyValueFromStoreUseCase.Input, Unit>(any(), any()) } returns Unit

        sut.process(context, null)

        verifySuspend {
            useCaseExecutor.await(
                useCase = removeUseCase,
                input = RemoveKeyValueFromStoreUseCase.Input("z".toKey())
            )
        }
    }

    @Test
    fun `process throws for unknown target`() = runTest {
        val action = ActionModelDomain.from("store://key-value/xxx?a=1")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        assertFailsWith<DomainException> {
            sut.process(context, null)
        }
    }

    @Test
    fun `process throws when query is null`() = runTest {
        val action = ActionModelDomain.from("store://key-value/save")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        assertFailsWith<DomainException> {
            sut.process(context, null)
        }
    }
}
