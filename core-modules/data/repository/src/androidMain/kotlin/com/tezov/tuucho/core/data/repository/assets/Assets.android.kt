package com.tezov.tuucho.core.data.repository.assets

import android.content.Context
import okio.Source
import okio.source

internal class AssetsAndroid(
    private val context: Context
) : AssetsProtocol {
    override fun readFile(
        path: String
    ): Source = context.assets.open("files/$path").source()
}
