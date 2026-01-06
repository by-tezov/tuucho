package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.di.Koin

sealed class ModuleGroupData : Koin.ModuleGroup {
    object Main : ModuleGroupData()

    object Assembler : ModuleGroupData() {
        sealed class ScopeContext : Koin.ScopeContext {
            override val group get() = Assembler

            object Material : ScopeContext()
        }
    }

    object Breaker : ModuleGroupData()

    object Rectifier : ModuleGroupData() {
        sealed class ScopeContext : Koin.ScopeContext {
            override val group get() = Rectifier

            object Material : ScopeContext()

            object Response : ScopeContext()
        }
    }

    object Shadower : ModuleGroupData()

    object Interceptor : ModuleGroupData()
}
