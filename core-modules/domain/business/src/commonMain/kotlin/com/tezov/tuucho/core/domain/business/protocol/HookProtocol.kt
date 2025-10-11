package com.tezov.tuucho.core.domain.business.protocol

sealed interface HookProtocol {

    interface BeforeNavigateToUrl : HookProtocol {
        suspend fun onEvent(currentUrl: String?, nextUrl: String): Boolean
    }

    interface AfterNavigateToUrl : HookProtocol {
        suspend fun onEvent(currentUrl: String)
    }

    interface BeforeNavigateBack : HookProtocol {
        suspend fun onEvent(currentUrl: String, nextUrl: String?): Boolean
    }

    interface AfterNavigateBack : HookProtocol {
        suspend fun onEvent(currentUrl: String?)
    }

}