package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject

sealed interface NavigationRepositoryProtocol {

    sealed class Event<out ELEMENT:Any> {

        object Clear : Event<Nothing>()

        data class SavedForReuse<ELEMENT:Any>(val fromIndex: Int, val element: ELEMENT) : Event<ELEMENT>()

        data class RemovedFromTail<ELEMENT:Any>(val elements: List<ELEMENT>) : Event<ELEMENT>()

        data class RemovedAtTail<ELEMENT:Any>(val element: ELEMENT) : Event<ELEMENT>()

        data class AddedAtTail<ELEMENT:Any>(val element: ELEMENT) : Event<ELEMENT>()

        data class ReuseRestoredAtTail<ELEMENT:Any>(val element: ELEMENT) : Event<ELEMENT>()

    }

    interface StackDestination {

        suspend fun stack(): List<NavigationDestination>

        suspend fun swallow(destination: NavigationDestination): List<Event<NavigationRoute>>

    }

    interface StackScreen {

        suspend fun getScreen(identifier: SourceIdentifierProtocol): ScreenProtocol?

        suspend fun getScreens(url: String): List<ScreenProtocol>?

        suspend fun swallow(
            events: List<Event<NavigationRoute>>,
            componentObject: JsonObject? = null,
        ): List<Event<ScreenProtocol.IdentifierProtocol>>

    }

    interface StackAnimator {

        val animate: Notifier.Collector<Boolean>

        suspend fun swallow(events: List<Event<ScreenProtocol.IdentifierProtocol>>, animationObject: JsonObject? = null)

        suspend fun getScreenIdentifiers(): List<ScreenProtocol.IdentifierProtocol>?

        fun notifyComplete()

    }

}

