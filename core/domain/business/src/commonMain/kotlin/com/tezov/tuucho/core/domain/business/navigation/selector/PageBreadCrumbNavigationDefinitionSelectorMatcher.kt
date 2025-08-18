package com.tezov.tuucho.core.domain.business.navigation.selector

import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol

class PageBreadCrumbNavigationDefinitionSelectorMatcher(
    private val values: List<String>,
) : NavigationDefinitionSelectorMatcherProtocol {


    fun accept(breadCrumb: List<String>): Boolean {
        if (breadCrumb.size < values.size) return false
        return breadCrumb.takeLast(values.size) == values
    }

}