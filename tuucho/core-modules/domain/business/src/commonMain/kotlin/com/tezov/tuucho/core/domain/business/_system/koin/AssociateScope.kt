@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass

class AssociateScopeDSL(
    val association: KClass<*>,
    val scopeDSL: ScopeDSL
) {
    @KoinDslMarker
    inline fun <reified T : Any> declaration(
        qualifier: Qualifier? = null
    ) {
        with(scopeDSL) {
            val factory = declaration<T>(qualifier)
            @Suppress("UNCHECKED_CAST")
            (factory as InstanceFactory<Any>) associate association
        }
    }

    // Single
    @KoinDslMarker
    inline fun <reified T> scoped(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ) {
        with(scopeDSL) {
            val koinDefinition = scoped(qualifier, definition)
            @Suppress("UNCHECKED_CAST")
            (koinDefinition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R> scopedOf(
        crossinline constructor: () -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val koinDefinition = scopedOf<R>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (koinDefinition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R, reified T1> scopedOf(
        crossinline constructor: (T1) -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val koinDefinition = scopedOf<R, T1>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (koinDefinition as KoinDefinition<Any>) associate association
        }
    }

    // Factory
    @KoinDslMarker
    inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ) {
        with(scopeDSL) {
            val koinDefinition = factory(qualifier, definition)
            @Suppress("UNCHECKED_CAST")
            (koinDefinition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R> factoryOf(
        crossinline constructor: () -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val koinDefinition = factoryOf<R>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (koinDefinition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R, reified T1> factoryOf(
        crossinline constructor: (T1) -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val koinDefinition = factoryOf<R, T1>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (koinDefinition as KoinDefinition<Any>) associate association
        }
    }

    // Misc.
    @KoinDslMarker
    fun factory(
        value: String
    ) {
        factory(named(value)) { value }
    }
}
