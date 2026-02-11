package com.tezov.tuucho.core.domain.business.interaction.exceptionHandler

import com.tezov.tuucho.core.domain.business.protocol.InterceptorProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol

object ShadowerExceptionHandler {

    fun interface Navigate : InterceptorProtocol<Navigate.Context> {
        data class Context(
            val screen: ScreenProtocol,
        )
    }

}


