@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class AssociateModule(
    val association: KClass<*>,
    val module: Module
) {
    @KoinDslMarker
    inline fun <reified T : Any> declaration(
        qualifier: Qualifier? = null
    ) {
        with(module) {
            val factory = declaration<T>(qualifier)
            @Suppress("UNCHECKED_CAST")
            (factory as InstanceFactory<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R> singleOf(
        crossinline constructor: () -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(module) {
            val definition = singleOf<R>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R, reified T1> singleOf(
        crossinline constructor: (T1) -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(module) {
            val definition = singleOf<R, T1>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }

    @KoinDslMarker
    inline fun <reified R> factoryOf(
        crossinline constructor: () -> R,
        noinline options: DefinitionOptions<R>? = null,
    ) {
        with(module) {
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
        with(module) {
            val definition = factoryOf<R, T1>(constructor, options)
            @Suppress("UNCHECKED_CAST")
            (definition as KoinDefinition<Any>) associate association
        }
    }
}
