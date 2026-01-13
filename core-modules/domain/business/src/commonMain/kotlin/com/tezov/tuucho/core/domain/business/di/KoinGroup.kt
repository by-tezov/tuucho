package com.tezov.tuucho.core.domain.business.di

import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL

typealias ModuleDeclaration = Module.() -> Unit
typealias ScopeDeclaration = ScopeDSL.() -> Unit

sealed class Koin {
    interface ModuleGroup

    interface ScopeContext : Qualifier {
        val group: ModuleGroup
        override val value: QualifierValue
            get() = TypeQualifier(this::class).value
    }

    abstract val group: ModuleGroup

    data class Module(
        override val group: ModuleGroup,
        val declaration: ModuleDeclaration
    ) : Koin()

    data class Scope(
        val scopeContext: ScopeContext,
        val declaration: ScopeDeclaration
    ) : Koin() {
        override val group get() = scopeContext.group
    }

    companion object {
        @KoinDslMarker
        fun module(
            group: ModuleGroup,
            declaration: ModuleDeclaration
        ) = Module(group, declaration)

        @KoinDslMarker
        fun scope(
            scopeContext: ScopeContext,
            declaration: ScopeDeclaration
        ) = Scope(scopeContext, declaration)
    }
}
