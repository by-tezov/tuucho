package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema.Key
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named

internal object MaterialBreakerModule {
    object Name {
        val BREAKABLES get() = named("MaterialBreakerModule.Name.BREAKABLE")
    }

    fun invoke() = module(ModuleGroupData.Breaker) {
        factory<List<String>>(Name.BREAKABLES) {
            with(Key) {
                listOf(
                    components,
                    contents,
                    styles,
                    options,
                    states,
                    texts,
                    colors,
                    dimensions,
                    actions,
                )
            }
        }
        singleOf(::MaterialBreaker)
    }
}
