package com.tezov.tuucho.core.domain.business._system.koin

import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL

typealias ModuleDeclaration = Module.() -> Unit
typealias ScopeDeclaration = ScopeDSL.() -> Unit

sealed class KoinMass {
    interface ModuleContext

    interface ScopeContext : Qualifier {
        val group: ModuleContext
        override val value: QualifierValue
            get() = TypeQualifier(this::class).value
    }

    abstract val group: ModuleContext

    data class Module(
        override val group: ModuleContext,
        val declaration: ModuleDeclaration
    ) : KoinMass()

    data class Scope(
        val scopeContext: ScopeContext,
        val declaration: ScopeDeclaration
    ) : KoinMass() {
        override val group get() = scopeContext.group
    }

    companion object {
        @KoinDslMarker
        fun module(
            group: ModuleContext,
            declaration: ModuleDeclaration
        ) = Module(group, declaration)

        @KoinDslMarker
        fun scope(
            scopeContext: ScopeContext,
            declaration: ScopeDeclaration
        ) = Scope(scopeContext, declaration)
    }
}
