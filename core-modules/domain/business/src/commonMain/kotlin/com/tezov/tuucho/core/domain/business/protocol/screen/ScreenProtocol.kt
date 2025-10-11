package com.tezov.tuucho.core.domain.business.protocol.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

interface ScreenProtocol {

    val route: NavigationRoute.Url

    val view: ViewProtocol

    suspend fun update(jsonObject: JsonObject)

    fun <V : ViewProtocol> views(klass: KClass<V>): List<V>
}