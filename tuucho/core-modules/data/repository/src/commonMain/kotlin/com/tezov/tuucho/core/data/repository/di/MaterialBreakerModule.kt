package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema.Key
import org.koin.plugin.module.dsl.single

internal object MaterialBreakerModule {
    fun invoke() = module(ModuleContextData.Breaker) {
        single<MaterialBreaker>()

        associate<MaterialBreaker.Association.Breakable> {
            with(Key) {
                factory(components)
                factory(contents)
                factory(styles)
                factory(options)
                factory(states)
                factory(texts)
                factory(colors)
                factory(dimensions)
                factory(actions)
                factory(images)
            }
        }
    }
}
