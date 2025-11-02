package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import kotlinx.serialization.json.JsonElement

interface ActionProcessorProtocol {
    object Priority {
        val LOW = 0
        val DEFAULT = 100
        val HIGH = 200
    }

    val priority: Int

    fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean

    suspend fun process(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    )
}
