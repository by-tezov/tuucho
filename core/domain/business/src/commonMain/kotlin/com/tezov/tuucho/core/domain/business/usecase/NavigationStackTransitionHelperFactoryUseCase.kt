package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.interaction.navigation.transition.DefaultNavigationTransitionStackHelper
import com.tezov.tuucho.core.domain.business.interaction.navigation.transition.FadeNavigationTransitionStackHelper
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.Type
import com.tezov.tuucho.core.domain.business.protocol.NavigationTransitionStackHelperProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigationStackTransitionHelperFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.NavigationStackTransitionHelperFactoryUseCase.Output
import kotlinx.serialization.json.JsonObject

class NavigationStackTransitionHelperFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {

    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val helper: NavigationTransitionStackHelperProtocol,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(input: Input) = with(input) {
        Output(
            helper = prototypeObject.withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
                .let { scope ->
                    when (scope.type) {
                        Type.fade -> FadeNavigationTransitionStackHelper()
                        else -> DefaultNavigationTransitionStackHelper()
                    }
                }
        )
    }

}