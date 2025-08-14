package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingOptionSelector
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingOptionSelector.Value.Type
import com.tezov.tuucho.core.domain.business.navigation.selector.PageBreadCrumbNavigationOptionSelector
import com.tezov.tuucho.core.domain.business.protocol.NavigationOptionSelectorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.SettingOptionSelectorFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.SettingOptionSelectorFactoryUseCase.Output
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class SettingOptionSelectorFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {

    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val selector: NavigationOptionSelectorProtocol,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(input: Input) = with(input) {
        Output(
            selector = prototypeObject.withScope(SettingOptionSelector::Scope)
                .let { scope ->
                    when (scope.type) {
                        Type.pageBreadCrumb -> PageBreadCrumbNavigationOptionSelector(
                            values = scope.values!!.jsonArray.map { it.jsonPrimitive.string },
                        )

                        else -> throw DomainException.Default("Selector $prototypeObject can't be resolved")
                    }
                } as NavigationOptionSelectorProtocol
        )
    }

}