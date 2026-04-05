package com.tezov.tuucho.core.domain.business.interaction.shadower

import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateShadowerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

class ContextualShadowerProcessor : NavigateShadowerUseCase.Processor {
    override val type: String
        get() = SettingComponentShadowerSchema.Key.contextual

    override suspend fun process(
        screen: ScreenProtocol,
        jsonObjects: Flow<JsonObject>
    ) {
        screen.update(jsonObjects)
    }
}
