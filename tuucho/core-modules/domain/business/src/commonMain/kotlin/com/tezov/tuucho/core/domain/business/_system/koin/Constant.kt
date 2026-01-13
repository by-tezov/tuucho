@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import org.koin.core.qualifier.StringQualifier

object Constant {
    val koinRootScopeQualifier = StringQualifier("_root_")
}
