package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSelectorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSelectorSchema.Value.Type
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase.Input
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class NavigationDefinitionSelectorMatcherFactoryUseCaseTest {
    private lateinit var sut: NavigationDefinitionSelectorMatcherFactoryUseCase

    @BeforeTest
    fun setup() {
        sut = NavigationDefinitionSelectorMatcherFactoryUseCase()
    }

    @Test
    fun `invoke returns PageBreadCrumbNavigationDefinitionSelectorMatcher when type is pageBreadCrumb`() {
        val prototype = buildJsonObject {
            put(ComponentSettingNavigationSelectorSchema.Key.type, Type.pageBreadCrumb)
            put(
                ComponentSettingNavigationSelectorSchema.Key.values,
                buildJsonArray {
                    add("home")
                    add("profile")
                    add("settings")
                }
            )
        }

        val input = Input(prototypeObject = prototype)

        val output = sut.invoke(input)

        assertIs<PageBreadCrumbNavigationDefinitionSelectorMatcher>(output.selector)
    }

    @Test
    fun `invoke throws DomainException when type is unsupported`() {
        val prototype = buildJsonObject {
            put(ComponentSettingNavigationSelectorSchema.Key.type, "unsupported-type")
        }

        val input = Input(prototypeObject = prototype)

        assertFailsWith<DomainException.Default> {
            sut.invoke(input)
        }
    }

    @Test
    fun `invoke throws NullPointerException when type is pageBreadCrumb but values is missing`() {
        val prototype = buildJsonObject {
            put(ComponentSettingNavigationSelectorSchema.Key.type, Type.pageBreadCrumb)
        }

        val input = Input(prototypeObject = prototype)

        assertFailsWith<NullPointerException> {
            sut.invoke(input)
        }
    }
}
