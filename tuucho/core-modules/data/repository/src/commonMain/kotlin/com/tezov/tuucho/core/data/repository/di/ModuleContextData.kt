package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.di.KoinMass

sealed class ModuleContextData : KoinMass.ModuleContext {
    object Main : ModuleContextData()

    object Assembler : ModuleContextData() {
        sealed class ScopeContext : KoinMass.ScopeContext {
            override val group get() = Assembler

            object Material : ScopeContext()

            object Response : ScopeContext() {
                object Form : ScopeContext()
            }
        }
    }

    object Breaker : ModuleContextData()

    object Rectifier : ModuleContextData() {
        sealed class ScopeContext : KoinMass.ScopeContext {
            override val group get() = Rectifier

            object Material : ScopeContext()

            object Response : ScopeContext()
        }
    }

    object Shadower : ModuleContextData()

    object Interceptor : ModuleContextData()
}
