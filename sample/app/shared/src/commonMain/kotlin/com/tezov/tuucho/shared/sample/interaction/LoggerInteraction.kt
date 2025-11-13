package com.tezov.tuucho.shared.sample.interaction

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.shared.sample._system.Logger
import kotlinx.serialization.json.JsonElement

class LoggerInteraction(
    private val logger: Logger
): ActionProcessorProtocol {
    override val priority: Int = ActionProcessorProtocol.Priority.LOW

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?
    ) = true

    override suspend fun process(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?
    ) {
        logger.debug("ACTION") { "from ${route.value}: $action" }
    }
}
