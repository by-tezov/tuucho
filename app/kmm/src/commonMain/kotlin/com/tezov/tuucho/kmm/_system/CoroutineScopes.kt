package com.tezov.tuucho.kmm._system

import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoroutineScopes : CoroutineScopesProtocol {

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        println("Coroutine failed in ${context[CoroutineName]}: ${throwable.message}")
        throw throwable
    }

    private val network = CoroutineScope(
        Dispatchers.IO +
                SupervisorJob() +
                CoroutineName("Network") +
                exceptionHandler
    )

    private val database = CoroutineScope(
        Dispatchers.IO +
                SupervisorJob() +
                CoroutineName("Database") +
                exceptionHandler
    )

    private val parser = CoroutineScope(
        Dispatchers.Default +
                SupervisorJob() +
                CoroutineName("Parser") +
                exceptionHandler
    )

    private val event = CoroutineScope(
        Dispatchers.Default +
                SupervisorJob() +
                CoroutineName("Event") +
                exceptionHandler
    )

    private val uiProcessor = CoroutineScope(
        Dispatchers.Default +
                SupervisorJob() +
                CoroutineName("UIProcessor") +
                exceptionHandler
    )

    override fun launchOnDatabase(block: suspend CoroutineScope.() -> Unit) {
        database.launch(block = block)
    }

    override suspend fun <T>onDatabase(block: suspend CoroutineScope.() -> T) =
        database.async(block = block).await()

    override fun launchOnNetwork(block: suspend CoroutineScope.() -> Unit) {
        network.launch(block = block)
    }

    override suspend fun <T>onNetwork(block: suspend CoroutineScope.() -> T) =
        network.async(block = block).await()

    override fun launchOnParser(block: suspend CoroutineScope.() -> Unit) {
        parser.launch(block = block)
    }

    override suspend fun <T>onParser(block: suspend CoroutineScope.() -> T) =
        parser.async(block = block).await()

    override fun launchOnEvent(block: suspend CoroutineScope.() -> Unit) {
        event.launch(block = block)
    }

    override suspend fun <T>onEvent(block: suspend CoroutineScope.() -> T) =
        event.async(block = block).await()

    override fun launchOnUiProcessor(block: suspend CoroutineScope.() -> Unit) {
        uiProcessor.launch(block = block)
    }

    override suspend fun <T>onUiProcessor(block: suspend CoroutineScope.() -> T) =
        uiProcessor.async(block = block).await()


}
