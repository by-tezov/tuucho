package com.tezov.tuucho.convention

class UiExtensionLibraryPlugin : AbstractLibraryPlugin() {

    override fun optIn() = listOf<String>(

    ).asIterable()

    override fun compilerOption() = listOf<String>(

    )

    override val hasAssets = false

}

