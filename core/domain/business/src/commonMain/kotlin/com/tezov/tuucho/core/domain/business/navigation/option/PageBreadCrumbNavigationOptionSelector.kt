package com.tezov.tuucho.core.domain.business.navigation.option

import com.tezov.tuucho.core.domain.business.protocol.NavigationOptionSelectorProtocol

class PageBreadCrumbNavigationOptionSelector(
    private val values: List<String>,
) : NavigationOptionSelectorProtocol {


    fun accept(breadCrumb: List<String>): Boolean {
        if (breadCrumb.size < values.size) return false
        return breadCrumb.takeLast(values.size) == values
    }

}