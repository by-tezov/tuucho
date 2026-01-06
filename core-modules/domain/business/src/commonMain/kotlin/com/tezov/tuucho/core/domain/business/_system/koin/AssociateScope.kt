@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.qualifier.Qualifier
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

    @KoinDslMarker
    inline fun <reified R> scopedOf(
        crossinline constructor: () -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val definition = scopedOf<R>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R, reified T1> scopedOf(
        crossinline constructor: (T1) -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val definition = scopedOf<R, T1>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R> factoryOf(
        crossinline constructor: () -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val definition = factoryOf<R>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R, reified T1> factoryOf(
        crossinline constructor: (T1) -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(scopeDSL) {
            val definition = factoryOf<R, T1>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }
}
