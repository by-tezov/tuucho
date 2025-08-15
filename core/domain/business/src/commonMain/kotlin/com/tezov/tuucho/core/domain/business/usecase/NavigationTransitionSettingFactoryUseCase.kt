package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationSchema.Transition
import com.tezov.tuucho.core.domain.business.navigation.transition.spec.TransitionSpec
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigationTransitionSettingFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.NavigationTransitionSettingFactoryUseCase.Output
import kotlinx.serialization.json.JsonObject

class NavigationTransitionSettingFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {

    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val spec: TransitionSpec,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(input: Input) = with(input) {
        Output(
            spec = prototypeObject.withScope(Transition.Spec::Scope)
                .let { scope ->
                    when (scope.type) {
                        Transition.Spec.Value.Type.fade -> TransitionSpec.Fade(
                            //TODO parameter duration_ms =
                        )
                        //TODO other
                        else -> throw DomainException.Default("transition spec $prototypeObject can't be resolved")
                    }
                } as TransitionSpec
        )
    }

}