package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema.Key
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.core.qualifier.named

internal object MaterialBreakerModule {
    object Name {
        val BREAKABLES = named("MaterialBreakerModule.Name.BREAKABLE")
    }

    fun invoke() = module(ModuleGroupData.Breaker) {
        factory<List<String>>(Name.BREAKABLES) {
            listOf(
                Key.components,
                Key.contents,
                Key.styles,
                Key.options,
                Key.states,
                Key.texts,
                Key.colors,
                Key.dimensions,
                Key.actions,
            )
        }
        single<MaterialBreaker> { MaterialBreaker() }
    }
}
