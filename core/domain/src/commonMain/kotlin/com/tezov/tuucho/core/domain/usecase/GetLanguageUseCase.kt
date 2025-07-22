package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.config.Language

class GetLanguageUseCase {

    fun invoke() = Language.Default //TODO retrieve system language or preference application
}