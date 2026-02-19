package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Retrieve
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class NavigationMaterialCacheRepositoryTest {

    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var retrieve: Retrieve
    private lateinit var stackScreen: StackScreen
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var navigationOptionSelectorFactory: NavigationDefinitionSelectorMatcherFactoryUseCase
    private lateinit var sut: NavigationMaterialCacheRepository

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        retrieve = mock()
        stackScreen = mock()
        useCaseExecutor = mock()
        navigationOptionSelectorFactory = mock()
        sut = NavigationMaterialCacheRepository(
            useCaseExecutor = useCaseExecutor,
            navigationOptionSelectorFactory = navigationOptionSelectorFactory
        ).apply {
            val fieldRetrieve = this.javaClass.getDeclaredField("retrieveMaterialRepository")
            fieldRetrieve.isAccessible = true
            fieldRetrieve.set(this, retrieve)
            val fieldStack = this.javaClass.getDeclaredField("navigationStackScreenRepository")
            fieldStack.isAccessible = true
            fieldStack.set(this, stackScreen)
        }
    }

    @Test
    fun `bind and unbind component cache behaves correctly`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-1", "value-1")
        sut.bindComponentObjectCache(route)
        sut.bindComponentObjectCache(route)
        everySuspend { retrieve.isValid("value-1") } returns true
        everySuspend { retrieve.process("value-1") } returns buildJsonObject {}
        sut.prepareNavigationConsumable("value-1")
        val obj = sut.getComponentObject("value-1")
        assertEquals(buildJsonObject {}, obj)
        sut.unbindComponentObjectCache(route)
        sut.unbindComponentObjectCache(route)
        assertFailsWith<ClassCastException> { runBlocking { sut.getComponentObject("value-1") } }
    }

    @Test
    fun `consumeNavigationSettingExtraObject empties after consumption`() = coroutineTestScope.run {
        everySuspend { retrieve.process("url-extra") } returns buildJsonObject { put("extra", "x") }
        everySuspend { retrieve.isValid("url-extra") } returns false
        sut.prepareNavigationConsumable("url-extra")
        val consumed = sut.consumeNavigationSettingExtraObject("url-extra")
        assertEquals(buildJsonObject { put("extra", "x") }, consumed)
        val consumedAgain = sut.consumeNavigationSettingExtraObject("url-extra")
        assertNull(consumedAgain)
    }

    @Test
    fun `consumeNavigationDefinitionOptionObject empties after consumption`() = coroutineTestScope.run {
        everySuspend { retrieve.process("url-option") } returns buildJsonObject { put("option", "o") }
        everySuspend { retrieve.isValid("url-option") } returns false
        sut.prepareNavigationConsumable("url-option")
        val consumed = sut.consumeNavigationDefinitionOptionObject("url-option")
        assertEquals(buildJsonObject { put("option", "o") }, consumed)
        val consumedAgain = sut.consumeNavigationDefinitionOptionObject("url-option")
        assertNull(consumedAgain)
    }

    @Test
    fun `consumeNavigationDefinitionTransitionObject empties after consumption`() = coroutineTestScope.run {
        everySuspend { retrieve.process("url-transition") } returns buildJsonObject { put("transition", "t") }
        everySuspend { retrieve.isValid("url-transition") } returns false
        sut.prepareNavigationConsumable("url-transition")
        val consumed = sut.consumeNavigationDefinitionTransitionObject("url-transition")
        assertEquals(buildJsonObject { put("transition", "t") }, consumed)
        val consumedAgain = sut.consumeNavigationDefinitionTransitionObject("url-transition")
        assertNull(consumedAgain)
    }
}
