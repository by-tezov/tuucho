package com.tezov.tuucho.core.presentation.ui.view.protocol

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasContextualUpdaterProtocol
import kotlinx.serialization.json.JsonObject
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

interface ViewProtocol :
    DomainViewProtocol,
    HasContextualUpdaterProtocol {
    suspend fun initialize(
        componentObject: JsonObject
    )

    @Composable
    fun display(
        scope: Any? = null
    )

    fun updateIfNeeded()
}
