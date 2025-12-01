package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.transition.DefaultNavigationTransitionStackHelper
import com.tezov.tuucho.core.domain.business.interaction.navigation.transition.FadeNavigationTransitionStackHelper
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.Type
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationStackTransitionHelperFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationStackTransitionHelperFactoryUseCase.Output
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class NavigationStackTransitionHelperFactoryUseCaseTest {
    private lateinit var sut: NavigationStackTransitionHelperFactoryUseCase

    @BeforeTest
    fun setup() {
        sut = NavigationStackTransitionHelperFactoryUseCase()
    }

    @Test
    fun `invoke returns FadeNavigationTransitionStackHelper when type is fade`() {
        val prototype = buildJsonObject {
            put("type", Type.fade)
        }

        val input = Input(
            prototypeObject = prototype
        )

        val output: Output = sut.invoke(input)

        assertIs<FadeNavigationTransitionStackHelper>(output.helper)
    }

    @Test
    fun `invoke returns DefaultNavigationTransitionStackHelper when type is not fade`() {
        val prototype = buildJsonObject {
            put("type", Type.slideHorizontal)
        }

        val input = Input(
            prototypeObject = prototype
        )

        val output: Output = sut.invoke(input)

        assertIs<DefaultNavigationTransitionStackHelper>(output.helper)
    }

    @Test
    fun `invoke returns DefaultNavigationTransitionStackHelper when type is missing`() {
        val prototype = buildJsonObject {
            put("nothing", JsonNull)
        }

        val input = Input(
            prototypeObject = prototype
        )

        val output: Output = sut.invoke(input)

        assertIs<DefaultNavigationTransitionStackHelper>(output.helper)
    }
}
