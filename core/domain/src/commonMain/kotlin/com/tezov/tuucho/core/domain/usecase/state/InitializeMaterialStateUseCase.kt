package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class InitializeMaterialStateUseCase(
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(url: String) {
        //TODO: current page is still visible by user
        // action on screen while this is fully processed can crash the app, need fail proof way
        // for now only one screen, should change when I will do the stack navigation
        materialState.clear()
    }

}
