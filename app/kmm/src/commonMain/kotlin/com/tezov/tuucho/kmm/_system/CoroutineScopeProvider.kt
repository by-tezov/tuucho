package com.tezov.tuucho.kmm._system

import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class CoroutineScopeProvider : CoroutineScopeProviderProtocol {

    private val networkJob = SupervisorJob()
    private val databaseJob = Job()
    private val parserJob = Job()
    private val eventJob = Job()
    private val uiProcessorJob = Job()

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        println("Coroutine failed in ${context[CoroutineName]}: ${throwable.message}")
        throw throwable
    }

    override val network = CoroutineScope(
        Dispatchers.IO +
                networkJob +
                CoroutineName("Network") +
                exceptionHandler
    )

    override val database = CoroutineScope(
        Dispatchers.IO +
                databaseJob +
                CoroutineName("Database") +
                exceptionHandler
    )

    override val parser = CoroutineScope(
        Dispatchers.Default +
                parserJob +
                CoroutineName("Parser") +
                exceptionHandler
    )

    override val event = CoroutineScope(
        Dispatchers.Default +
                eventJob + CoroutineName("Event") +
                exceptionHandler
    )

    override val uiProcessor = CoroutineScope(
        Dispatchers.Default +
                uiProcessorJob +
                CoroutineName("UIProcessor") +
                exceptionHandler
    )

}
