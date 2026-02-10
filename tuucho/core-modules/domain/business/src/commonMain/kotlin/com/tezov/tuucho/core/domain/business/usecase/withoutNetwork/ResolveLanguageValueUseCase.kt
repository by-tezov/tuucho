package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.ResolveLanguageValueUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.ResolveLanguageValueUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonObject

@OpenForTest
class ResolveLanguageValueUseCase(
    private val platformRepository: SystemPlatformRepositoryProtocol
) : UseCaseProtocol.Async<Input, Output> {
    data class Input(
        val resolvedKey: String,
        val jsonObject: JsonObject,
    )

    data class Output(
        val value: String?,
    )

    override suspend fun invoke(
        input: Input
    ): Output {
        val language = platformRepository.getCurrentLanguage()
        val resolved = language.tag?.let { input.jsonObject[it].stringOrNull }
            ?: input.jsonObject[language.code].stringOrNull
            ?: run {
                input.jsonObject.firstNotNullOfOrNull {
                    if (language.matchCodeFallback(it.key)) {
                        it.value.stringOrNull
                    } else {
                        null
                    }
                }
            }
            ?: input.jsonObject[input.resolvedKey].stringOrNull
        return Output(value = resolved)
    }

    private fun LanguageModelDomain.matchCodeFallback(
        key: String
    ): Boolean {
        val code = key.split("-", limit = 2).first()
        return code == this.code
    }
}
