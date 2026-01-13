package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema.Key
import org.koin.core.module.dsl.singleOf

internal object MaterialBreakerModule {
    fun invoke() = module(ModuleGroupData.Breaker) {
        singleOf(::MaterialBreaker)

        associate<MaterialBreaker.Association.Breakable> {
            with(Key) {
                factory(components)
                factory(contents)
                factory(styles)
                factory(options)
                factory(states)
                factory(texts)
                factory(colors)
                factory(colors)
                factory(actions)
            }
        }
    }
}
