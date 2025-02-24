package com.tezov.tuucho.core.domain.item

sealed class ActionItemDomain(
    val value: String
) {

    //TODO

}

class NavigateItemDomain(
    value: String
) : ActionItemDomain(value) {

    //TODO

}
