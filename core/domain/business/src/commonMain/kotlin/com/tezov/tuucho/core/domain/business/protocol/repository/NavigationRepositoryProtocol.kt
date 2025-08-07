package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject

sealed interface NavigationRepositoryProtocol {

    interface Destination {

        sealed class Event {

            object Clear : Event()

            data class SavedForReuse(val fromIndex: Int, val destination: NavigationDestination) :
                Event()

            data class RemovedFromTail(val destinations: List<NavigationDestination>) : Event()

            data class RemovedAtTail(val destination: NavigationDestination) : Event()

            data class AddedAtTail(val destination: NavigationDestination) : Event()

            data class ReuseRestoredAtTail(val destination: NavigationDestination) : Event()

        }

        suspend fun swallow(destination: NavigationDestination): List<Event>

    }

    interface StackScreen {

        val events: Notifier.Collector<ScreenProtocol.IdentifierProtocol>

        fun getView(identifier: SourceIdentifierProtocol): ScreenProtocol?

        fun getViews(url: String): List<ScreenProtocol>?

        suspend fun swallow(events: List<Destination.Event>, componentObject: JsonObject? = null)

    }

}

