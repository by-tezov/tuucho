@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import androidx.compose.runtime.Composable
import kotlinx.serialization.json.JsonObject
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

interface ViewProtocol : DomainViewProtocol {
    val children: List<ViewProtocol>?

    suspend fun update(
        jsonObject: JsonObject
    )

    @Composable
    fun display(
        scope: Any? = null
    )
}
