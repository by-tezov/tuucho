package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business._system.koin.KoinIsolatedContext
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Retrieve
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.resetCalls
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class NavigationMaterialCacheRepositoryTest {

    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var navigationDefinitionSelectorMatcherFactory: NavigationDefinitionSelectorMatcherFactoryUseCase

    private lateinit var retrieveMaterialRepository: Retrieve
    private lateinit var navigationStackScreenRepository: StackScreen

    private lateinit var sut: NavigationMaterialCacheRepository

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        navigationDefinitionSelectorMatcherFactory = mock()
        retrieveMaterialRepository = mock()
        navigationStackScreenRepository = mock()

        @OptIn(TuuchoInternalApi::class)
        KoinIsolatedContext.koinApplication = koinApplication {
            modules(module {
                single { retrieveMaterialRepository }
                single { navigationStackScreenRepository }
            })
        }

        sut = NavigationMaterialCacheRepository(
            useCaseExecutor = useCaseExecutor,
            navigationDefinitionSelectorMatcherFactory = navigationDefinitionSelectorMatcherFactory
        )
    }

    @AfterTest
    fun tearDown() {
        @OptIn(TuuchoInternalApi::class)
        KoinIsolatedContext.koinApplication?.close()
        verifyNoMoreCalls(
            useCaseExecutor,
            navigationDefinitionSelectorMatcherFactory,
            retrieveMaterialRepository,
            navigationStackScreenRepository
        )
    }

    @Test
    fun `prepare consumable with previous null component`() = runTest {
        val expectedObject = buildJsonObject {
            put("key", "value")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns expectedObject

        sut.prepareNavigationConsumable(route.value)
        val result = sut.getComponentObject(route.value)

        assertEquals(expectedObject, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `prepare consumable with previous not null component and invalid`() = runTest {
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {}
        everySuspend { retrieveMaterialRepository.isValid(any()) } returns false

        sut.prepareNavigationConsumable(route.value)
        resetCalls(retrieveMaterialRepository)

        sut.prepareNavigationConsumable(route.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.isValid(route.value)
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `prepare consumable with previous not null component and valid`() = runTest {
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {}
        everySuspend { retrieveMaterialRepository.isValid(any()) } returns true

        sut.prepareNavigationConsumable(route.value)
        resetCalls(retrieveMaterialRepository)

        sut.prepareNavigationConsumable(route.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.isValid(route.value)
        }
    }

    @Test
    fun `shadower forward can be prepared and consume once`() = runTest {
        val expectedObject = buildJsonObject {
            put("key", "value")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.shadower, buildJsonObject {
                    put(SettingComponentShadowerSchema.Key.navigateForward, expectedObject)
                    put(SettingComponentShadowerSchema.Key.navigateBackward, buildJsonObject { })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        val result = sut.consumeShadowerSettingObject(route.value, SettingComponentShadowerSchema.Key.navigateForward)

        assertEquals(expectedObject, result)
        assertNull(sut.consumeShadowerSettingObject(route.value, SettingComponentShadowerSchema.Key.navigateForward))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `shadower backward can be prepared and consume once`() = runTest {
        val expectedObject = buildJsonObject {
            put("key", "value")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.shadower, buildJsonObject {
                    put(SettingComponentShadowerSchema.Key.navigateBackward, expectedObject)
                    put(SettingComponentShadowerSchema.Key.navigateForward, buildJsonObject { })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        val result = sut.consumeShadowerSettingObject(route.value, SettingComponentShadowerSchema.Key.navigateBackward)

        assertEquals(expectedObject, result)
        assertNull(sut.consumeShadowerSettingObject(route.value, SettingComponentShadowerSchema.Key.navigateBackward))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `navigation extra can be prepared and consume once`() = runTest {
        val expectedObject = buildJsonObject {
            put("key", "value")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.navigation, buildJsonObject {
                    put(ComponentSettingNavigationSchema.Key.extra, expectedObject)
                    put(ComponentSettingNavigationSchema.Key.definitions, buildJsonArray { })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        val result = sut.consumeNavigationSettingExtraObject(route.value)

        assertEquals(expectedObject, result)
        assertNull(sut.consumeNavigationSettingExtraObject(route.value))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `navigation definition option can be prepared and consume once`() = runTest {
        val expectedObject = buildJsonObject {
            put("key", "value")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.navigation, buildJsonObject {
                    put(ComponentSettingNavigationSchema.Key.extra, buildJsonObject { })
                    put(ComponentSettingNavigationSchema.Key.definitions, buildJsonArray {
                        add(buildJsonObject {
                            put(ComponentSettingNavigationSchema.Definition.Key.transition, buildJsonObject { })
                            put(ComponentSettingNavigationSchema.Definition.Key.option, expectedObject)
                        })
                    })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        val result = sut.consumeNavigationDefinitionOptionObject(route.value)

        assertEquals(expectedObject, result)
        assertNull(sut.consumeNavigationDefinitionOptionObject(route.value))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `navigation definition transition can be prepared and consume once`() = runTest {
        val expectedObject = buildJsonObject {
            put("key", "value")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.navigation, buildJsonObject {
                    put(ComponentSettingNavigationSchema.Key.extra, buildJsonObject { })
                    put(ComponentSettingNavigationSchema.Key.definitions, buildJsonArray {
                        add(buildJsonObject {
                            put(ComponentSettingNavigationSchema.Definition.Key.option, buildJsonObject { })
                            put(ComponentSettingNavigationSchema.Definition.Key.transition, expectedObject)
                        })
                    })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        val result = sut.consumeNavigationDefinitionTransitionObject(route.value)

        assertEquals(expectedObject, result)
        assertNull(sut.consumeNavigationDefinitionTransitionObject(route.value))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `navigation definition option + transition can be prepared and consume once with selector`() = runTest {
        val expectedOptionObject = buildJsonObject {
            put("key-option", "value-option")
        }
        val expectedTransitionObject = buildJsonObject {
            put("key-transition", "value-transition")
        }
        val selectorObject = buildJsonObject {
            put("key-selector", "value-selector")
        }
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.navigation, buildJsonObject {
                    put(ComponentSettingNavigationSchema.Key.extra, buildJsonObject { })
                    put(ComponentSettingNavigationSchema.Key.definitions, buildJsonArray {
                        add(buildJsonObject {
                            put(ComponentSettingNavigationSchema.Definition.Key.selector, selectorObject)
                            put(ComponentSettingNavigationSchema.Definition.Key.option, expectedOptionObject)
                            put(ComponentSettingNavigationSchema.Definition.Key.transition, expectedTransitionObject)
                        })
                        add(buildJsonObject {
                            put(ComponentSettingNavigationSchema.Definition.Key.option, buildJsonObject { })
                            put(ComponentSettingNavigationSchema.Definition.Key.transition, buildJsonObject { })
                        })
                    })
                })
            })
        }

        everySuspend { navigationStackScreenRepository.routes() } returns listOf(route)

        everySuspend {
            useCaseExecutor.await<
                NavigationDefinitionSelectorMatcherFactoryUseCase.Input,
                NavigationDefinitionSelectorMatcherFactoryUseCase.Output
                >(any(), any())
        } returns NavigationDefinitionSelectorMatcherFactoryUseCase.Output(
            selector = PageBreadCrumbNavigationDefinitionSelectorMatcher(values = listOf(route.value))
        )

        sut.prepareNavigationConsumable(route.value)
        val resultTransition = sut.consumeNavigationDefinitionTransitionObject(route.value)
        val resultOption = sut.consumeNavigationDefinitionOptionObject(route.value)

        assertEquals(expectedOptionObject, resultOption)
        assertEquals(expectedTransitionObject, resultTransition)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.process(route.value)
            useCaseExecutor.await(navigationDefinitionSelectorMatcherFactory, any())
            navigationStackScreenRepository.routes()
        }
    }

    @Test
    fun `previous component do not create side effect on next component`() = runTest {
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.shadower, buildJsonObject {
                    put(SettingComponentShadowerSchema.Key.navigateForward, buildJsonObject { })
                })
                put(ComponentSettingSchema.Root.Key.navigation, buildJsonObject {
                    put(ComponentSettingNavigationSchema.Key.extra, buildJsonObject { })
                    put(ComponentSettingNavigationSchema.Key.definitions, buildJsonArray {
                        add(buildJsonObject {
                            put(ComponentSettingNavigationSchema.Definition.Key.option, buildJsonObject { })
                            put(ComponentSettingNavigationSchema.Definition.Key.transition, buildJsonObject { })
                        })
                    })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        resetCalls(retrieveMaterialRepository)

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject { }
        everySuspend { retrieveMaterialRepository.isValid(any()) } returns false
        sut.prepareNavigationConsumable(route.value)

        assertNull(sut.consumeShadowerSettingObject(route.value, SettingComponentShadowerSchema.Key.navigateForward))
        assertNull(sut.consumeNavigationSettingExtraObject(route.value))
        assertNull(sut.consumeNavigationDefinitionOptionObject(route.value))
        assertNull(sut.consumeNavigationDefinitionTransitionObject(route.value))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.isValid(route.value)
            retrieveMaterialRepository.process(route.value)
        }
    }

    @Test
    fun `previous component can be reuse when still valid`() = runTest {
        val route = NavigationRoute.Url("id-1", "value-1")

        everySuspend { retrieveMaterialRepository.process(any()) } returns buildJsonObject {
            put(TypeSchema.Value.Setting.prefix, buildJsonObject {
                put(ComponentSettingSchema.Root.Key.shadower, buildJsonObject {
                    put(SettingComponentShadowerSchema.Key.navigateForward, buildJsonObject { })
                })
                put(ComponentSettingSchema.Root.Key.navigation, buildJsonObject {
                    put(ComponentSettingNavigationSchema.Key.extra, buildJsonObject { })
                    put(ComponentSettingNavigationSchema.Key.definitions, buildJsonArray {
                        add(buildJsonObject {
                            put(ComponentSettingNavigationSchema.Definition.Key.option, buildJsonObject { })
                            put(ComponentSettingNavigationSchema.Definition.Key.transition, buildJsonObject { })
                        })
                    })
                })
            })
        }

        sut.prepareNavigationConsumable(route.value)
        resetCalls(retrieveMaterialRepository)

        everySuspend { retrieveMaterialRepository.isValid(any()) } returns true
        sut.prepareNavigationConsumable(route.value)

        assertNotNull(sut.consumeShadowerSettingObject(route.value, SettingComponentShadowerSchema.Key.navigateForward))
        assertNotNull(sut.consumeNavigationSettingExtraObject(route.value))
        assertNotNull(sut.consumeNavigationDefinitionOptionObject(route.value))
        assertNotNull(sut.consumeNavigationDefinitionTransitionObject(route.value))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            retrieveMaterialRepository.isValid(route.value)
        }
    }

}
