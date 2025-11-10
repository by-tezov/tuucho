package com.tezov.tuucho.core.domain.tool.extension

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ResolutionContext
import org.koin.core.module.OptionDslMarker
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import kotlin.reflect.KClass

object ExtensionKoin {
    @OptIn(KoinInternalApi::class)
    @OptionDslMarker
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
    inline fun <reified T : Any> Koin.getAllOrdered(): List<T> = scopeRegistry.rootScope.getAllOrdered()

    @OptIn(KoinInternalApi::class)
    inline fun <reified T : Any> Scope.getAllOrdered(): List<T> = with(getKoin()) {
        val typeName = T::class.qualifiedName ?: error("class qualifier name is null")
        val orderedInstances = instanceRegistry.instances.entries
            .filter { (key, _) ->
                key.contains("$typeName#ordered#")
            }.mapNotNull { (key, factory) ->
                val index = key
                    .substringAfter("#ordered#", "")
                    .substringBefore(':')
                    .toIntOrNull() ?: return@mapNotNull null
                index to factory
            }.sortedBy { it.first }
            .map { (_, factory) ->
                @Suppress("UNCHECKED_CAST")
                factory as InstanceFactory<T>
                val beanDefinition = factory.beanDefinition
                val scopeId = beanDefinition.scopeQualifier.value
                val scope = getScopeOrNull(scopeId) ?: scopeRegistry.rootScope
                val context = ResolutionContext(
                    logger = logger,
                    scope = scope,
                    clazz = beanDefinition.primaryType,
                    qualifier = beanDefinition.qualifier,
                    parameters = null
                )
                factory.get(context)
            }.toMutableList()
        return orderedInstances
    }
}
