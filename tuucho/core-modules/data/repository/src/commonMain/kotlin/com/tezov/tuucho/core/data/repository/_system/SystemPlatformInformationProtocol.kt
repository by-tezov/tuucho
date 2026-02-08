@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.ContinuationInterceptor

interface SystemPlatformInformationProtocol {

    suspend fun currentDispatcher() = currentCoroutineContext()[ContinuationInterceptor]

    fun currentThreadName(): String

    fun currentLanguage(): String

    fun currentCountry(): String?
}
