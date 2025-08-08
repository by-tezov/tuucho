package com.tezov.tuucho.core.presentation.ui.animator

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol as DomainScreenProtocol

class Animator(
    private val screen: ScreenProtocol,
) : ScreenProtocol {

    private val isVisible: Boolean = true
    private val isTransitioning: Boolean = false
    private val animation: Any = false



    @Composable
    override fun display(scope: Any?) {
        screen.display(scope)
    }

    override val identifier: DomainScreenProtocol.IdentifierProtocol
        get() = screen.identifier

    override suspend fun update(jsonObject: JsonObject) {
        screen.update(jsonObject)
    }

    override fun <V : ViewProtocol> views(
        klass: KClass<V>,
    ) = screen.views(klass)
}