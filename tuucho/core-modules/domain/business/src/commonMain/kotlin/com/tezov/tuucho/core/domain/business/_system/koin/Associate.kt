@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business.exception.DomainException
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ResolutionContext
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.ext.getFullName
import kotlin.reflect.KClass

object Associate {

    infix fun <T : Any> InstanceFactory<T>.associate(
        clazz: KClass<*>
    ) {
        beanDefinition.secondaryTypes += clazz
    }

    @OptIn(KoinInternalApi::class)
    infix fun <S : Any> KoinDefinition<out S>.associate(
        clazz: KClass<*>
    ): KoinDefinition<out S> {
        factory.associate(clazz)
        val mapping =
            indexKey(clazz, factory.beanDefinition.qualifier, factory.beanDefinition.scopeQualifier)
        module.mappings[mapping] = factory
        return this
    }

    @OptIn(KoinInternalApi::class)
    inline fun <reified T : Any> Module.declaration(
        qualifier: Qualifier? = null
    ): InstanceFactory<T> {
        val mapping = indexKey(T::class, qualifier, Constant.koinRootScopeQualifier)
        @Suppress("UNCHECKED_CAST")
        return (mappings[mapping] as? InstanceFactory<T>)
            ?: throw DomainException.Default("${T::class.getFullName()} not found in module")
    }

    @OptIn(KoinInternalApi::class)
    inline fun <reified T : Any> ScopeDSL.declaration(
        qualifier: Qualifier? = null
    ): InstanceFactory<T> {
        val mapping = indexKey(T::class, qualifier, scopeQualifier)
        @Suppress("UNCHECKED_CAST")
        return (module.mappings[mapping] as? InstanceFactory<T>)
            ?: throw DomainException.Default("${T::class.getFullName()} not found in scope")
    }

    @OptIn(KoinInternalApi::class)
    inline fun <reified T : Any> Koin.getAllAssociated(
        clazz: KClass<*>
    ): List<T> {
        val instanceContext = ResolutionContext(logger, scopeRegistry.rootScope, clazz)
        instanceContext.scopeArchetype = scopeRegistry.rootScope.scopeArchetype
        return instanceRegistry.instances.values
            .filter { factory ->
                (factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeQualifier ||
                    factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeArchetype
                ) &&
                    (factory.beanDefinition.primaryType == clazz || factory.beanDefinition.secondaryTypes.contains(clazz))
            }.distinct()
            .sortedWith(compareBy { it.beanDefinition.toString() })
            .mapNotNull { it.get(instanceContext) as? T } // TODO linked scope, can't do because it is internal
    }

    @OptIn(KoinInternalApi::class)
    inline fun <reified T : Any> Scope.getAllAssociated(
        clazz: KClass<*>
    ): List<T> = with(getKoin()) {
        val instanceContext = ResolutionContext(logger, this@getAllAssociated, clazz)
        instanceContext.scopeArchetype = this@getAllAssociated.scopeArchetype
        instanceRegistry.instances.values
            .filter { factory ->
                // TODO linked scope
                (factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeQualifier ||
                    factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeArchetype
                ) &&
                    (factory.beanDefinition.primaryType == clazz || factory.beanDefinition.secondaryTypes.contains(clazz))
            }.distinct()
            .sortedWith(compareBy { it.beanDefinition.toString() })
            .mapNotNull { it.get(instanceContext) as? T }
    }

    inline fun <reified T : Any> Module.associate(
        associateDSL: AssociateModuleDSL.() -> Unit
    ) {
        AssociateModuleDSL(T::class, this).associateDSL()
    }

    inline fun <reified T : Any> ScopeDSL.associate(
        associateDSL: AssociateScopeDSL.() -> Unit
    ) {
        AssociateScopeDSL(T::class, this).associateDSL()
    }
}
