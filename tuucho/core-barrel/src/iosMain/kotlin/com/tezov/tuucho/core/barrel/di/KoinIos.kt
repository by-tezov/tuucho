@file:OptIn(BetaInteropApi::class)

package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class KoinIos() : TuuchoKoinComponent {

    private fun getKotlinClass(objCClass: ObjCClass): KClass<*>? = getOriginalKotlinClass(objCClass)

    private fun getKotlinClass(objCProtocol: ObjCProtocol): KClass<*>? = getOriginalKotlinClass(objCProtocol)

    fun get(
        clazz: ObjCClass,
    ): Any = get(
        clazz = clazz,
        qualifier = null,
        parameters = null,
    )

    fun get(
        clazz: ObjCClass,
        qualifier: Qualifier?
    ): Any = get(
        clazz = clazz,
        qualifier = qualifier,
        parameters = null,
    )

    fun get(
        clazz: ObjCClass,
        qualifier: Qualifier?,
        parameters: ParametersDefinition?
    ): Any {
        val kclazz = getKotlinClass(clazz) ?: throw DomainException.Default("kclass not found for objCClass $clazz")
        return getKoin().get(clazz = kclazz, qualifier = qualifier, parameters = parameters)
    }

    fun get(
        clazz: ObjCProtocol,
    ): Any = get(
        clazz = clazz,
        qualifier = null,
        parameters = null,
    )

    fun get(
        clazz: ObjCProtocol,
        qualifier: Qualifier?
    ): Any = get(
        clazz = clazz,
        qualifier = qualifier,
        parameters = null,
    )

    fun get(
        clazz: ObjCProtocol,
        qualifier: Qualifier?,
        parameters: ParametersDefinition?
    ): Any {
        val kclazz = getKotlinClass(clazz) ?: throw DomainException.Default("kclass not found for objCClass $clazz")
        return getKoin().get(clazz = kclazz, qualifier = qualifier, parameters = parameters)
    }

    fun close() { getKoin().close() }
}


