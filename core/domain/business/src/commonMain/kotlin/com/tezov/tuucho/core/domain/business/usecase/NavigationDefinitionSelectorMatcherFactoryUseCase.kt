package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSelectorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSelectorSchema.Value.Type
import com.tezov.tuucho.core.domain.business.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigationDefinitionSelectorMatcherFactoryUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.NavigationDefinitionSelectorMatcherFactoryUseCase.Output
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class NavigationDefinitionSelectorMatcherFactoryUseCase : UseCaseProtocol.Sync<Input, Output> {

    data class Input(
        val prototypeObject: JsonObject,
    )

    data class Output(
        val selector: NavigationDefinitionSelectorMatcherProtocol,
    )

    @Suppress("UNCHECKED_CAST")
    override fun invoke(input: Input) = with(input) {
        Output(
            selector = prototypeObject.withScope(ComponentSettingNavigationSelectorSchema::Scope)
                .let { scope ->
                    when (scope.type) {
                        Type.pageBreadCrumb -> PageBreadCrumbNavigationDefinitionSelectorMatcher(
                            values = scope.values!!.jsonArray.map { it.jsonPrimitive.string },
                        )

                        else -> throw DomainException.Default("Selector $prototypeObject can't be resolved")
                    }
                } as NavigationDefinitionSelectorMatcherProtocol
        )
    }

}