package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.ClearTransientMaterialCacheRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol
import kotlinx.coroutines.runBlocking

class InitializeViewStateUseCase(
    private val screenState: ScreenStateProtocol,
    private val clearTransientMaterialCacheRepository: ClearTransientMaterialCacheRepositoryProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(url: String) {
        //TODO: current page is still visible by user
        // action on screen while this is fully processed can crash the app, need fail proof way
        // for now only one screen, should change when I will do the stack navigation
        screenState.url.takeIf { it.isNotBlank() }?.let {
            runBlocking {
                clearTransientMaterialCacheRepository.process(screenState.url)
            }
        }
        screenState.clear()
        screenState.url = url
    }

}
