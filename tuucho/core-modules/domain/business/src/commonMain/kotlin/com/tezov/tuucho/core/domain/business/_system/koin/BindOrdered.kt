@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.definition.indexKey
import org.koin.core.instance.ResolutionContext
import org.koin.core.module.KoinDslMarker
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import kotlin.reflect.KClass

object BindOrdered {
    @OptIn(KoinInternalApi::class)
    @KoinDslMarker
    infix fun <S : Any> KoinDefinition<out S>.bindOrdered(
        clazz: KClass<S>
    ): KoinDefinition<out S> {
        val index = module.mappings.values
            .distinctBy { it.beanDefinition }
            .count { it.beanDefinition.secondaryTypes.contains(clazz) }
        bind(clazz)
        val orderedQualifier = named("${clazz.qualifiedName}#ordered#$index")
        val mapping = indexKey(clazz, orderedQualifier, factory.beanDefinition.scopeQualifier)
        module.mappings[mapping] = factory
        return this
    }

    @OptIn(KoinInternalApi::class)
    @KoinDslMarker
    inline fun <reified T : Any> Koin.getAllOrdered(): List<T> = scopeRegistry.rootScope.getAllOrdered(T::class)

    @KoinDslMarker
    inline fun <reified T : Any> Scope.getAllOrdered(): List<T> = getAllOrdered(T::class)

    @OptIn(KoinInternalApi::class)
    @KoinDslMarker
    fun <T : Any> Scope.getAllOrdered(
        clazz: KClass<T>
    ): List<T> = with(getKoin()) {
        val typeName = clazz.qualifiedName ?: error("class qualifier name is null")
        instanceRegistry.instances.entries
            .filter { (key, _) ->
                // TODO scope protection and linked scope
                key.contains("$typeName#ordered#")
            }.mapNotNull { (key, factory) ->
                val index = key
                    .substringAfter("#ordered#", "")
                    .substringBefore(':')
                    .toIntOrNull() ?: return@mapNotNull null
                index to factory
            }.sortedBy { it.first }
            .map { (_, factory) ->
                val beanDefinition = factory.beanDefinition
                val context = ResolutionContext(
                    logger = logger,
                    scope = this@getAllOrdered,
                    clazz = beanDefinition.primaryType,
                    qualifier = beanDefinition.qualifier,
                    parameters = null
                )
                @Suppress("UNCHECKED_CAST")
                factory.get(context) as T
            }.toList()
    }
}
