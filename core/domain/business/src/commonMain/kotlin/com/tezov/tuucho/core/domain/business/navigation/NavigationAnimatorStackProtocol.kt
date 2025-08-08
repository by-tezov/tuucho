package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.navigation.animation.NavigationAnimationType
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.Event
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackAnimator
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationAnimatorStackProtocol(
    private val coroutineScopes: CoroutineScopesProtocol,
) : StackAnimator, KoinComponent {

    data class Item(
        val identifierProtocol: ScreenProtocol.IdentifierProtocol,
        val animation: NavigationAnimationType,
        val requestToRemove: Boolean
    )

    private val _animate = Notifier.Emitter<Boolean>()
    override val animate get() = _animate.createCollector

    private val stack = mutableListOf<Item>()

    override suspend fun getScreenIdentifiers(): List<ScreenProtocol.IdentifierProtocol>? {
        TODO("Not yet implemented")
    }

    override fun notifyComplete() {
        TODO("Not yet implemented")
    }

    override suspend fun swallow(
        events: List<Event<ScreenProtocol.IdentifierProtocol>>,
        animationObject: JsonObject?,
    ) = coroutineScopes.navigation.on {
        val reuseBin = mutableListOf<Item>()
        for (event in events) {
            when (event) {
                is Event.Clear -> clear()
                is Event.SavedForReuse -> savedForReuse(event, reuseBin)
                is Event.RemovedFromTail -> removedFromTail(event)
                is Event.ReuseRestoredAtTail -> reuseRestoredAtTail(event, reuseBin)
                is Event.RemovedAtTail -> removedAtTail(event)
                is Event.AddedAtTail -> addedAtTail(
                    event, animationObject
                        ?: throw DomainException.Default("componentObject can't be null")
                )
            }
        }
    }

    private fun clear() {
        TODO("Not yet implemented")
    }

    private fun savedForReuse(
        event: Event.SavedForReuse<ScreenProtocol.IdentifierProtocol>,
        reuseBin: MutableList<Item>,
    ) {
        TODO("Not yet implemented")
    }

    private fun removedFromTail(
        event: Event.RemovedFromTail<ScreenProtocol.IdentifierProtocol>,
    ) {
        TODO("Not yet implemented")
    }

    private fun removedAtTail(
        event: Event.RemovedAtTail<ScreenProtocol.IdentifierProtocol>,
    ) {
        TODO("Not yet implemented")
    }

    private suspend fun addedAtTail(
        event: Event.AddedAtTail<ScreenProtocol.IdentifierProtocol>,
        componentObject: JsonObject,
    ) {
        TODO("Not yet implemented")
    }

    private fun reuseRestoredAtTail(
        event: Event.ReuseRestoredAtTail<ScreenProtocol.IdentifierProtocol>,
        reuseBin: MutableList<Item>,
    ) {
        TODO("Not yet implemented")
    }

}