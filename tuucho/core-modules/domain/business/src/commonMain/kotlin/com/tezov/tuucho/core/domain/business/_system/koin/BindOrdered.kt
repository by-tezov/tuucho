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
        val typeName = clazz.qualifiedName ?: error("class qualified name is null")
        val orderedQualifier = named("$typeName#ordered#$index")
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
        val typeName = clazz.qualifiedName ?: error("class qualified name is null")
        val instanceContext = ResolutionContext(logger, scopeRegistry.rootScope, clazz)
        instanceContext.scopeArchetype = scopeRegistry.rootScope.scopeArchetype
        instanceRegistry.instances.entries
            .filter { (key, factory) ->
                key.contains("$typeName#ordered#") &&
                    (
                        (factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeQualifier ||
                            factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeArchetype
                        ) &&
                            (factory.beanDefinition.primaryType == clazz || factory.beanDefinition.secondaryTypes.contains(clazz))
                    )
            }.distinct()
            .mapNotNull { (key, factory) ->
                val index = key
                    .substringAfter("#ordered#", "")
                    .substringBefore(':')
                    .toIntOrNull() ?: return@mapNotNull null
                index to factory
            }.sortedBy { it.first }
            .mapNotNull {
                @Suppress("UNCHECKED_CAST")
                it.second.get(instanceContext) as? T
            } // TODO linked scope, can't do because it is internal
    }
}
