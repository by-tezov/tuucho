@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system

import okio.Path

interface Platform {

    fun pathFromCacheFolder(relativePath: String): Path

}

